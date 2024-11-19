package faang.school.postservice.service.feed;

import faang.school.postservice.kafka.dto.PostKafkaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
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

    public void addFeed(PostKafkaDto postKafkaDto) {
        Long postId = postKafkaDto.getPostId();
        var score = postKafkaDto.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        List<Long> followerIds = postKafkaDto.getFollowerIds();
        followerIds.forEach(id -> feedZSetOperations.add(buildKey(id), postId, score));
    }

    private String buildKey(Long userId) {
        return KEY + userId;
    }
}
