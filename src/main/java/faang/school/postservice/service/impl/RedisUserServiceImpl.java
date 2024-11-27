package faang.school.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.dto.redis.cache.UserFields;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.RedisTransactional;
import faang.school.postservice.service.RedisUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisUserServiceImpl implements RedisUserService, RedisTransactional {
    private static final String KEY_PREFIX = "user:";

    @Value("${redis.feed.ttl.user:86400}")
    private long userTtlInSeconds;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserShortInfoRepository userShortInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisUserServiceImpl(@Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                UserShortInfoRepository userShortInfoRepository) {
        this.redisTemplate = redisTemplate;
        this.userShortInfoRepository = userShortInfoRepository;
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void saveUserIfNotExists(RedisUserDto userDto) {
        String key = createKey(userDto.getUserId());
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("User with ID {} already exists in Redis, skipping...", userDto.getUserId());
            return;
        }
        saveUser(userDto);
    }

    @Override
    public RedisUserDto getUser(Long userId) {
        String key = createKey(userId);
        Map<String, Object> userMap = fetchAndCacheUserIfAbsent(userId, key);
        return convertMapToUserDto(userMap);
    }

    @Override
    public List<Long> getFollowerIds(Long userId) {
        String key = createKey(userId);
        String serializedFollowerIds = (String) redisTemplate.opsForHash().get(key, UserFields.FOLLOWER_IDS);
        return deserializeFollowerIds(serializedFollowerIds);
    }


    private Map<String, Object> fetchAndCacheUserIfAbsent(Long userId, String key) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            log.warn("User with ID {} not found in Redis, fetching from database", userId);
            RedisUserDto userFromDb = fetchUserFromDatabase(userId);
            saveUser(userFromDb);
            return convertUserDtoToMap(userFromDb);
        }

        Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);
        return redisData.entrySet().stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey().toString(), e.getValue()), Map::putAll);
    }

    private RedisUserDto fetchUserFromDatabase(Long userId) {
        UserShortInfo userShortInfo = userShortInfoRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Short info about user with id = %d not found in DB", userId)));
        return new RedisUserDto(
                userShortInfo.getUserId(),
                userShortInfo.getUsername(),
                userShortInfo.getFileId(),
                userShortInfo.getSmallFileId(),
                deserializeFollowerIds(userShortInfo.getFollowerIds()));
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void saveUser(RedisUserDto redisUserDto) {
        String key = createKey(redisUserDto.getUserId());
        executeRedisTransaction(() -> {
            Map<String, Object> userMap = convertUserDtoToMap(redisUserDto);
            userMap.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> redisTemplate.opsForHash().put(key, entry.getKey(), entry.getValue()));
            updateTtl(key);
        });
    }

    private String createKey(Long userId) {
        return KEY_PREFIX + userId;
    }

    private void updateTtl(String key) {
        redisTemplate.expire(key, userTtlInSeconds, TimeUnit.SECONDS);
    }

    private Map<String, Object> convertUserDtoToMap(RedisUserDto userDto) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(UserFields.USER_ID, userDto.getUserId().toString());
        userMap.put(UserFields.USERNAME, userDto.getUsername());
        userMap.put(UserFields.FILE_ID, userDto.getFileId());
        userMap.put(UserFields.SMALL_FILE_ID, userDto.getSmallFileId());
        userMap.put(UserFields.FOLLOWER_IDS, serializeFollowerIds(userDto.getFollowerIds()));
        return userMap;
    }

    private RedisUserDto convertMapToUserDto(Map<String, Object> userMap) {
        RedisUserDto userDto = new RedisUserDto();
        userDto.setUserId(Long.valueOf(userMap.get(UserFields.USER_ID).toString()));
        userDto.setUsername((String) userMap.get(UserFields.USERNAME));
        userDto.setFileId((String) userMap.get(UserFields.FILE_ID));
        userDto.setSmallFileId((String) userMap.get(UserFields.SMALL_FILE_ID));
        userDto.setFollowerIds(deserializeFollowerIds((String) userMap.get(UserFields.FOLLOWER_IDS)));
        return userDto;
    }

    private String serializeFollowerIds(List<Long> followerIds) {
        try {
            return objectMapper.writeValueAsString(followerIds);
        } catch (JsonProcessingException e) {
            //TODO кастомизированное исключение добавить
            throw new RuntimeException("Failed to serialize followerIds", e);
        }
    }

    private List<Long> deserializeFollowerIds(String followerIds) {
        if (followerIds == null || followerIds.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(followerIds, new TypeReference<List<Long>>() {
            });
        } catch (JsonProcessingException e) {
            //TODO кастомизированное исключение добавить
            throw new RuntimeException("Failed to deserialize followerIds", e);
        }
    }
}

