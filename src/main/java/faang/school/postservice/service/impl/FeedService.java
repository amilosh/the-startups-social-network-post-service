package faang.school.postservice.service.impl;

import faang.school.postservice.exception.RedisTransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisTemplate<String, Object> feedRedisTemplate;

    @Value("${spring.data.redis.feed.key-prefix:feed:}")
    private String feedPrefix;

    @Retryable(maxAttempts = 5, retryFor = RedisTransactionException.class)
    public void bindPostToFollower(Long followerId, Long postId) {
        String key = feedPrefix + followerId;
        feedRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                try {
                    ZSetOperations<String, Object> zSetOps = operations.opsForZSet();
                    zSetOps.add(key, postId, System.currentTimeMillis());
                    zSetOps.removeRange(key, 0, -501);
                    List<Object> execResult = operations.exec();
                    if (execResult.isEmpty()) {
                        throw new RedisTransactionException(
                                "Post %s was not added to user %s".formatted(postId, followerId));
                    }
                } catch (Exception exception) {
                    operations.discard();
                    throw exception;
                } finally {
                    operations.unwatch();
                }
                return null;
            }
        });
    }
}
