package faang.school.postservice.service.impl;

import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.RedisUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUserServiceImpl implements RedisUserService {
    private static final String KEY_PREFIX = "user:";
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String FILE_ID = "fileId";
    private static final String SMALL_FILE_ID = "smallFileId";

    @Value("${redis.feed.ttl.user:86400}")
    private long userTtlInSeconds;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserShortInfoRepository userShortInfoRepository;

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
                userShortInfo.getSmallFileId());
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void saveUser(RedisUserDto redisUserDto) {
        String key = createKey(redisUserDto.getUserId());
        executeRedisTransaction(() -> {
            Map<String, Object> userMap = convertUserDtoToMap(redisUserDto);
            userMap.forEach((field, value) -> redisTemplate.opsForHash().put(key, field, value));
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
        userMap.put(USER_ID, userDto.getUserId());
        userMap.put(USERNAME, userDto.getUsername());
        userMap.put(FILE_ID, userDto.getFileId());
        userMap.put(SMALL_FILE_ID, userDto.getSmallFileId());
        return userMap;
    }

    private RedisUserDto convertMapToUserDto(Map<String, Object> userMap) {
        RedisUserDto userDto = new RedisUserDto();
        userDto.setUserId(Long.valueOf(userMap.get(USER_ID).toString()));
        userDto.setUsername((String) userMap.get(USERNAME));
        userDto.setFileId((String) userMap.get(FILE_ID));
        userDto.setSmallFileId((String) userMap.get(SMALL_FILE_ID));
        return userDto;
    }

    private void executeRedisTransaction(Runnable transaction) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.multi();
            transaction.run();
            connection.exec();
            return null;
        });
    }
}

