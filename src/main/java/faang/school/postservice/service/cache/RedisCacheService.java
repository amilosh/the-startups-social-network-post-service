package faang.school.postservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisCacheService<T> implements CacheService<T> {

    private final RedisTemplate<String, T> redisTemplate;

    @Override
    public void put(String key, T value, Duration time) {
        redisTemplate.opsForValue().set(key, value, time);
    }
}
