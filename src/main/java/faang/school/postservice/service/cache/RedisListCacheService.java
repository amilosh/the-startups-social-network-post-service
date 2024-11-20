package faang.school.postservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisListCacheService<T> implements ListCacheService<T> {

    private final RedisTemplate<String, T> redisTemplate;

    @Override
    public void rightPush(String listKey, T value) {
        redisTemplate.opsForList().rightPush(listKey, value);
    }
}
