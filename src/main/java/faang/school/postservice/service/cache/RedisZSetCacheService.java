package faang.school.postservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.RedisTransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisZSetCacheService<T> implements SortedSetCacheService<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void put(String key, T value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public long size(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return Objects.requireNonNullElse(size, 0L);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Retryable(retryFor = RedisTransactionException.class,
            maxAttemptsExpression = "${spring.data.redis.transaction.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.data.redis.transaction.retry.backoff.delay}"))
    public void runInOptimisticLock(Runnable task, String key) {
        var operation = new SessionCallback<>() {
            public List<Object> execute(RedisOperations operations) {
                operations.watch(key);
                operations.multi();

                task.run();

                List<Object> result = operations.exec();
                operations.unwatch();

                if (result.isEmpty()) {
                    operations.discard();
                    throw new RedisTransactionException();
                }
                return result;
            }
        };

        redisTemplate.execute(operation);
    }


    @Override
    public Optional<T> popMin(String sortedSetKey, Class<T> clazz) {
        var tuple = redisTemplate.opsForZSet().popMin(sortedSetKey);
        return Optional.ofNullable(tuple)
                .map(ZSetOperations.TypedTuple::getValue)
                .map(value -> objectMapper.convertValue(value, clazz));
    }
}
