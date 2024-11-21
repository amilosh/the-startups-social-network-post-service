package faang.school.postservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RedisCacheService<T> implements CacheService<T> {

    private final RedisTemplate<String, T> redisTemplate;

    @Override
    public void set(String key, T value, Duration time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    @Override
    public long incrementAndGet(String key) {
        Long counter = redisTemplate.opsForValue().increment(key);
        return Objects.requireNonNullElse(counter, 0L);
    }
}
