package faang.school.postservice.repository;

import faang.school.postservice.cache.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNullElse;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsFeedRedisRepository {
    private final RedisTransactionRetryWrapper<String, Long> longRedisTemplateWrapper;

    @Value("${spring.data.redis.cache.news-feed.capacity}")
    private long feedCapacity;

    @Value("${spring.data.redis.cache.news-feed.post-batch-size}")
    private int batchSize;

    public void addPostId(Long postId, Long userId, LocalDateTime publishedAt) {
        String key = "news-feed-" + userId;
        long millis = convertToMillis(publishedAt);
        longRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(key);
            long feedSize = requireNonNullElse(operations.opsForZSet().size(key), 0L);
            operations.multi();
            if (feedSize == feedCapacity) {
                operations.opsForZSet().popMin(key);
            }
            operations.opsForZSet().add(key, postId, millis);
            return operations.exec();
        });
    }

    public void addAllPost(List<PostCache> posts, Long userId) {
        String key = "news-feed-" + userId;
        longRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(key);
            operations.multi();
            posts.forEach(post -> {
                Long postId = post.getId();
                long publishedAt = convertToMillis(post.getPublishedAt());
                operations.opsForZSet().add(key, postId, publishedAt);
            });
            return operations.exec();
        });

    }

    public List<Long> getPostIdsBatch(Long userId, Long firstExclusivePostId) {
        String key = "news-feed-" + userId;
        Set<Long> postIdsSet = longRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(key);
            double score = requireNonNullElse(operations.opsForZSet().score(key, firstExclusivePostId), 0.0);
            operations.multi();
            operations.opsForZSet().reverseRangeByScore(key, score, Double.MAX_VALUE, 0, batchSize + 1);
            return operations.exec();
        });

        List<Long> postIds = new ArrayList<>(postIdsSet);
        postIds.remove(postIdsSet.size() - 1);
        return postIds;
    }

    public List<Long> getPostIdsFirstBatch(Long userId) {
        String key = "news-feed-" + userId;
        Set<Long> postIdsSet = longRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(key);
            operations.multi();
            operations.opsForZSet().reverseRangeByScore(key, 0, Double.MAX_VALUE, 0, batchSize);
            return operations.exec();
        });
        return new ArrayList<>(postIdsSet);
    }

    private long convertToMillis(LocalDateTime time) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = time.atZone(zoneId);
        return zonedDateTime.toInstant().toEpochMilli();
    }
}





