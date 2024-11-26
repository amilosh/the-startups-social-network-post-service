package faang.school.postservice.service.feed;

import faang.school.postservice.annotations.SendPostViewEventToKafka;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.dto.redis.PostRedisEntity;
import faang.school.postservice.kafka.dto.PostKafkaDto;
import faang.school.postservice.mapper.feed.FeedMapper;
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

        Set<Long> postIds = feedZSetOperations.range(key, postsRange.getLeft(), postsRange.getRight());

        List<PostRedisEntity> posts = postService.getRedisPostsById(postIds);
        return feedMapper.mapToCommentFeedResponseDto(posts);
    }

    public void addFeed(PostKafkaDto postKafkaDto) {
        Long postId = postKafkaDto.getPostId();
        long score = postKafkaDto.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        List<Long> followerIds = postKafkaDto.getFollowerIds();
        followerIds.forEach(id -> addPostToFeed(buildKey(id), postId, score));
    }

    private String buildKey(Long userId) {
        return KEY + userId;
    }

    private Pair<Long, Long> defineRange(String key, Long postId) {
        long start;
        long end;
        if (postId == null) {
            start = 0;
            end = postsBatchSize - 1;
        } else {
            Long rank = feedZSetOperations.rank(key, postId);
            if (rank == null) {
                start = 0;
                end = postsBatchSize - 1;
            } else {
                start = rank + 1;
                end = rank + postsBatchSize;
            }
        }

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
