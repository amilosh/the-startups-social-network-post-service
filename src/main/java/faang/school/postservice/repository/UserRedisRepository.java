package faang.school.postservice.repository;

import faang.school.postservice.cache.UserCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserCacheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

@Repository
@RequiredArgsConstructor
public class UserRedisRepository {
    private final RedisTemplate<String, UserCache> userCacheRedisTemplate;
    private final UserServiceClient userServiceClient;
    private final UserCacheMapper userCacheMapper;
    private final UserContext userContext;

    @Value("${spring.data.redis.cache.user.ttl}")
    private Long ttl;

    @Async
    public void getAndSave(Long userId) {
        userContext.setUserId(userId);
        UserDto userDto = userServiceClient.getUser(userId);
        UserCache userCache = userCacheMapper.toUserCache(userDto);
        String key = "user-" + userId;
        userCacheRedisTemplate.opsForValue().set(key, userCache, ttl, SECONDS);
    }

    public void save(UserCache userCache) {
        String key = "user-" + userCache.getId();
        userCacheRedisTemplate.opsForValue().set(key, userCache, ttl, SECONDS);
    }

    public Optional<UserCache> findById(Long userId) {
        String key = "user-" + userId;
        return Optional.ofNullable(userCacheRedisTemplate.opsForValue().get(key));
    }
}
