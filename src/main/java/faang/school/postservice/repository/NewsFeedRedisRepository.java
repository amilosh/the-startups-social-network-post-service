package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNullElse;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsFeedRedisRepository {
    private final RedisTransactionRetryWrapper<Long, Long> retryWrapper;

    @Value("${spring.data.redis.cache.news-feed.capacity}")
    private long feedCapacity;

    @Value("${spring.data.redis.cache.news-feed.batch-size}")
    private int batchSize;

    public void addPostId(Long postId, Long userId, long createdAt) {
        retryWrapper.executeWithRetry(operations -> {
            operations.watch(userId);

            long feedSize = requireNonNullElse(operations.opsForZSet().size(userId), 0L);

            operations.multi();

            if (feedSize == feedCapacity) {
                operations.opsForZSet().popMin(userId);
            }
            operations.opsForZSet().add(userId, postId, createdAt);

            return operations.exec();
        });
    }

    public List<Long> getPostIdsBatch(Long userId, Long firstExclusivePostId) {
        Set<Long> postIdsSet = retryWrapper.executeWithRetry(operations -> {
            operations.watch(userId);

            double score = Objects.requireNonNullElse(operations.opsForZSet().score(userId, firstExclusivePostId), 0.0);

            operations.multi();

            operations.opsForZSet().reverseRangeByScore(userId, score, Double.MAX_VALUE, 0, batchSize + 1);

            return operations.exec();
        });

        List<Long> postIds = new ArrayList<>(postIdsSet);
        postIds.remove(postIdsSet.size() - 1);
        return postIds;
    }

    public List<Long> getPostIdsFirstBatch(Long userId) {
        Set<Long> postIdsSet = retryWrapper.executeWithRetry(operations -> {
            operations.watch(userId);
            operations.multi();

            operations.opsForZSet().reverseRangeByScore(userId, 0, Double.MAX_VALUE, 0, batchSize);

            return operations.exec();
        });

        return new ArrayList<>(postIdsSet);
    }
}





