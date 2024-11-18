package faang.school.postservice.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class ZSetRepository {
    private final ZSetOperations<String, String> stringZSetOperations;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisOperations redisOperations;

    @Value("${spring.data.redis.ttl.feed.user_feed_hour}")
    private int userFeedTTL;

    public void setAndRemoveRange(String key, String value, long timestamp, long limit) {
        redisOperations.executeInMulti(stringRedisTemplate, key, () -> {
            stringZSetOperations.add(key, value, timestamp);
            stringZSetOperations.removeRange(key, 0, limit);
            stringRedisTemplate.expire(key, Duration.ofHours(userFeedTTL));
        });
    }

    public Set<String> getValuesInRange(String key, long offset, long limit) {
        Set<String> keys = stringZSetOperations.reverseRange(key, offset, limit);

        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.expire(key, Duration.ofHours(userFeedTTL));
        }
        return keys;
    }

    public void saveTuplesByKey(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        if (!tuples.isEmpty()) {
            redisOperations.executeInMulti(stringRedisTemplate, key, () -> stringZSetOperations.add(key, tuples));
        }
    }
}
