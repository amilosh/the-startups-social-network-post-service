package faang.school.postservice.service.feed;

import faang.school.postservice.annotations.kafka.SendPostViewEventToKafka;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.kafka.post.event.PostPublishedKafkaEvent;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.post.PostRedis;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FeedService {
    private static final String KEY = "userPostsFeed:";

    @Value("${feed.posts-batch-size}")
    private long postsBatchSize;

    @Value("${redis.feed.max-size:500}")
    private int feedMaxSize;

    private final ZSetOperations<String, Long> feedZSetOperations;
    private final PostService postService;
    private final FeedMapper feedMapper;

    @SendPostViewEventToKafka(value = List.class, elementType = PostFeedResponseDto.class)
    public List<PostFeedResponseDto> getFeed(long userId, Long postId) {
        String key = buildKey(userId);

        Pair<Long, Long> postsRange = defineRange(key, postId);

        Set<Long> postIds = feedZSetOperations.reverseRange(key, postsRange.getLeft(), postsRange.getRight());

        List<PostRedis> posts = postService.getRedisPostsById(postIds);
        return feedMapper.mapToCommentFeedResponseDto(posts);
    }

    public void addFeed(PostPublishedKafkaEvent postPublishedKafkaEvent) {
        Long postId = postPublishedKafkaEvent.getPostId();
        long score = postPublishedKafkaEvent.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        List<Long> followerIds = postPublishedKafkaEvent.getFollowerIds();
        followerIds.forEach(id -> addPostToFeed(buildKey(id), postId, score));
    }

    private String buildKey(Long userId) {
        return KEY + userId;
    }

    private Pair<Long, Long> defineRange(String key, Long postId) {
        if (postId == null) {
            return Pair.of(0L, postsBatchSize - 1);
        }

        Long feedSize = feedZSetOperations.zCard(key);
        Long rank = feedZSetOperations.rank(key, postId);

        long start = feedSize - rank;
        long end = start + postsBatchSize - 1;

        return Pair.of(start, end);
    }

    private void addPostToFeed(String key, Long postId, long score) {
        feedZSetOperations.add(key, postId, score);

        var feedSize = feedZSetOperations.zCard(key);
        if (feedSize != null && feedSize > feedMaxSize) {
            feedZSetOperations.removeRange(key, 0, feedSize - feedMaxSize);
        }
    }
}
