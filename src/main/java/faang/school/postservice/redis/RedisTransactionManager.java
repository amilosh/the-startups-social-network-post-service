package faang.school.postservice.redis;

import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static faang.school.postservice.redis.RedisTransactionResult.LOCK_EXCEPTION;
import static faang.school.postservice.redis.RedisTransactionResult.NOT_FOUND;
import static faang.school.postservice.redis.RedisTransactionResult.SUCCESS;
import static java.lang.Boolean.FALSE;

@Service
public class RedisTransactionManager<K, V> {
    public <K, V> RedisTransactionResult executeRedisOperations(K key,
                                                                RedisTemplate<K, V> redisTemplate,
                                                                Consumer<RedisOperations<K, V>> redisOperations) {
        return redisTemplate.execute(new SessionCallback<>() {
            public RedisTransactionResult execute(@NotNull RedisOperations operations) throws DataAccessException {
                if (FALSE.equals(operations.hasKey(key))) {
                    return NOT_FOUND;
                }

                operations.watch(key);
                operations.multi();

                redisOperations.accept(operations);

                List<Object> transactionResult = operations.exec();
                if (transactionResult == null || transactionResult.isEmpty()) {
                    return LOCK_EXCEPTION;
                } else {
                    return SUCCESS;
                }
            }
        });
    }

    public <K, V> RedisTransactionResult updateRedisEntity(final K key,
                                                           RedisTemplate<K, V> redisTemplate,
                                                           BiConsumer<V, RedisOperations<K, V>> redisOperations) {
        return redisTemplate.execute(new SessionCallback<>() {
            public RedisTransactionResult execute(RedisOperations operations) throws DataAccessException {
                if (FALSE.equals(operations.hasKey(key))) {
                    return NOT_FOUND;
                }

                operations.watch(key);
                V redisEntity = (V) operations.opsForValue().get(key);
                operations.multi();

                redisOperations.accept(redisEntity, operations);

                List<Object> transactionResult = operations.exec();
                if (transactionResult == null || transactionResult.isEmpty()) {
                    return LOCK_EXCEPTION;
                } else {
                    return SUCCESS;
                }
            }
        });
    }
}
