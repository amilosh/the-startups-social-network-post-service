package faang.school.postservice.repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Range.Bound.exclusive;
import static org.springframework.data.domain.Range.Bound.unbounded;
import static org.springframework.data.domain.Range.from;
import static org.springframework.data.redis.connection.Limit.limit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsFeedRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.cache.feed.capacity}")
    private long feedCapacity;

    @Value("${spring.data.redis.cache.feed.batch-size}")
    private int batchSize;

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public void addPost(Long postId, Long userId, Long createdAt) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.watch(userId.toString());

                boolean isFull = false;
                Long size = operations.opsForZSet().size(userId.toString());
                if (size == feedCapacity) {
                    isFull = true;
                }

                operations.multi();

                if (isFull) {
                    operations.opsForZSet().popMin(userId.toString());
                }

                operations.opsForZSet().add(userId.toString(), postId.toString(), createdAt);

                List<Object> result = operations.exec();
                if (result.isEmpty()) {
                    throw new OptimisticLockingFailureException("Optimistic lock");
                }
                return result;
            }
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public List<Long> getPostBatch(Long userId, Long beginPostId) {
        return redisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Long> execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.watch(userId.toString());
                operations.multi();

                operations.opsForZSet()
                        .reverseRangeByLex(userId.toString(), from(unbounded()).to(exclusive(beginPostId.toString())), limit().count(batchSize));

                List<Object> result = operations.exec();
                if (result.isEmpty()) {
                    throw new OptimisticLockingFailureException("Optimistic lock");
                }

                Set<Object> feed = (Set<Object>) result.get(0);
                return feed.stream()
                        .map(obj -> Long.parseLong(obj.toString()))
                        .toList();
            }
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public List<Long> getPostBatch(Long userId) {
        return redisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Long> execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.watch(userId.toString());
                operations.multi();

                operations.opsForZSet()
                        .reverseRangeByLex(userId.toString(), from(unbounded()).to(unbounded()), limit().count(batchSize));

                List<Object> result = operations.exec();
                if (result.isEmpty()) {
                    throw new OptimisticLockingFailureException("Optimistic lock");
                }

                Set<Object> feed = (Set<Object>) result.get(0);
                return feed.stream()
                        .map(obj -> Long.parseLong(obj.toString()))
                        .toList();
            }
        });
    }
}





