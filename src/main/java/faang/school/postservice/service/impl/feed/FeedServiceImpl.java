package faang.school.postservice.service.impl.feed;

import faang.school.postservice.exception.RedisTransactionFailedException;
import faang.school.postservice.service.FeedService;
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
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<String, Object> feedRedisTemplate;
    @Value("${spring.data.redis.feed.key-prefix}")
    private String feedPrefix;

    @Retryable(maxAttempts = 3, retryFor = RedisTransactionFailedException.class)
    public void bindPostToFollower(Long followerId, Long postId) {
        String key = feedPrefix + followerId;

        feedRedisTemplate.execute(new SessionCallback<Object>() {
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
                        throw new RedisTransactionFailedException(
                                "Post %s was not added to user %s".formatted(postId, followerId));
                    }
                } catch (Exception e) {
                    operations.discard();
                    throw e;
                } finally {
                    operations.unwatch();
                }

                return null;
            }
        });
    }
}
