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

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsFeedRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.cache.feed-capacity}")
    private long feedCapacity;

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public void addPost(String postId, String userId, Long createdAt) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.watch(userId);

                boolean isFull = false;
                Long size = operations.opsForZSet().size(userId);
                if (size == feedCapacity) {
                    isFull = true;
                }

                operations.multi();

                if (isFull) {
                    operations.opsForZSet().popMin(userId);
                }

                operations.opsForZSet().add(userId, postId, createdAt);

                List<Object> result = operations.exec();
                if (result.isEmpty()) {
                    throw new OptimisticLockingFailureException("Optimistic lock");
                }
                return result;
            }
        });
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    public List<String> getFeed(String userId) {
        return redisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<String> execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.watch(userId);
                operations.multi();

                operations.opsForZSet().range(userId, 0, -1);

                List<Object> result = operations.exec();
                if (result.isEmpty()) {
                    throw new OptimisticLockingFailureException("Optimistic lock");
                }

                Set<Object> feed = (Set<Object>) result.get(0);
                return feed.stream()
                        .map(Object::toString)
                        .toList();
            }
        });
    }
}





