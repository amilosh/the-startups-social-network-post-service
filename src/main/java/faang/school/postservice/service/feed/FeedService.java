package faang.school.postservice.service.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FeedService {
    private static final String KEY = "user:";

    @Value("${feed.posts-batch-size}")
    long batchSize;

    private final ZSetOperations<String, Long> feedZSetOperations;

    public void getFeed(long userId, Long postId) {
        String key = buildKey(userId);

        long start;
        long end;
        if (postId == null) {
            start = 0;
            end = batchSize - 1;
        } else {
            Long rank = feedZSetOperations.rank(buildKey(userId), postId);
            if (rank == null) {
                start = 0;
                end = batchSize - 1;
            } else {
                start = rank + 1;
                end = rank + batchSize;
            }
        }

        Set<Long> postIds = feedZSetOperations.range(key, start, end);
    }

    public void addFeed(Long userId, Long postId, LocalDateTime publishedAt) {
        var score = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        feedZSetOperations.add(buildKey(userId), postId, score);
    }

    private String buildKey(Long userId) {
        return KEY + userId;
    }
}
