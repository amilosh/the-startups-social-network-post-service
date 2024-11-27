package faang.school.postservice.repository.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisOperations {
    private final RedisTemplate<String, String> stringValueRedisTemplate;
    private final RedisTemplate<String, Long> longValueRedisTemplate;
    private final RedisTransaction redisTransaction;

    public void executeInMulti(RedisTemplate<?, ?> redisTemplate, String key, Runnable runnable) {
        redisTransaction.execute(redisTemplate, key, operations -> {
            operations.multi();
            runnable.run();
            return operations.exec();
        });
    }

    public <T> void assignFieldByCounter(String counterKey, String objectKey, RedisTemplate<String, T> redisTemplate,
                                         Duration duration, BiConsumer<T, Long> consumer) {
        redisTransaction.execute(redisTemplate, objectKey, operations -> {
            String counterValueStr = stringValueRedisTemplate.opsForValue().get(counterKey);
            T post = redisTemplate.opsForValue().get(objectKey);

            if (counterValueStr == null || post == null) {
                return List.of(false);
            }

            long counterValue = Long.parseLong(counterValueStr);
            consumer.accept(post, counterValue);

            operations.multi();

            longValueRedisTemplate.opsForValue().decrement(counterKey, counterValue);
            redisTemplate.opsForValue().set(objectKey, post, duration);

            return operations.exec();
        });
    }
}
