package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserCacheRepository {
    private final RedisTemplate<String, UserDto> userDtoRedisTemplate;
    private final RedisOperations redisOperations;

    @Value("${app.post.cache.news_feed.prefix.user_id}")
    private String userIdPrefix;

    @Value("${spring.data.redis.ttl.feed.user_hour}")
    private int userTTL;

    public void save(UserDto user) {
        String key = buildId(user.getId());
        redisOperations.executeInMulti(userDtoRedisTemplate, key, () ->
                userDtoRedisTemplate.opsForValue().set(key, user, Duration.ofHours(userTTL)));
    }

    public void saveAll(Collection<UserDto> userDtoList) {
        userDtoList.forEach(this::save);
    }

    public Optional<UserDto> findById(long id) {
        return Optional.ofNullable(userDtoRedisTemplate.opsForValue().get(buildId(id)));
    }

    private String buildId(long id) {
        return userIdPrefix + id;
    }
}
