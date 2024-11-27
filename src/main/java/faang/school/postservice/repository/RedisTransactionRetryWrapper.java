package faang.school.postservice.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.Function;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

@RequiredArgsConstructor
public class RedisTransactionRetryWrapper<KEY, VAL> {
    private final RedisTemplate<KEY, VAL> redisTemplate;
    private final Environment environment;

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "#{@environment.getProperty('spring.data.redis.cache.optimistic-lock-retry.max-attempt')}",
            backoff = @Backoff(delayExpression = "#{@environment.getProperty('spring.data.redis.cache.optimistic-lock-retry.delay')}")
    )
    public <R> R executeWithRetry(Function<RedisOperations<KEY, VAL>, List<Object>> function) {
        List<Object> result = redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> List<Object> execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                return function.apply((RedisOperations<KEY, VAL>) operations);
            }
        });

        if (result.isEmpty()) {
            throw new OptimisticLockingFailureException("Optimistic Lock");
        }

        return (R) result.get(0);
    }
}
