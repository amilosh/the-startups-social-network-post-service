package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.message.PostEvent;
import faang.school.postservice.service.newsFeed.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    private RedisTemplate<String, Object> redisTemplate;
    private ZSetOperations<String, Object> zSetOperations;
    private FeedService feedService;
    private int maxFeedSize = 10;
    private PostEvent postEvent;

    @BeforeEach
    void setup() {

        redisTemplate = Mockito.mock(RedisTemplate.class);
        zSetOperations = Mockito.mock(ZSetOperations.class);

        feedService = new FeedService(redisTemplate, maxFeedSize);

        postEvent = PostEvent.builder()
                .postId(12345)
                .authorId(67890)
                .publishedAt(LocalDateTime.of(2023, 10, 25, 14, 30))
                .subscribers(List.of(1001L, 1002L))
                .build();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void addPostToFeed_WhenFeedSizeWithinLimit_ShouldAddPost() {
        String feedKey1 = "feed:1001";
        String feedKey2 = "feed:1002";

        when(zSetOperations.size(feedKey1)).thenReturn(5L);
        when(zSetOperations.size(feedKey2)).thenReturn(4L);

        feedService.addPostToFeed(postEvent);

        verify(zSetOperations).add(feedKey1, postEvent.getPostId(), postEvent.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        verify(zSetOperations).add(feedKey2, postEvent.getPostId(), postEvent.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli());

        verify(zSetOperations, never()).removeRange(feedKey1, 0, maxFeedSize - 6);
        verify(zSetOperations, never()).removeRange(feedKey2, 0, maxFeedSize - 5);
    }

    @Test
    void addPostToFeed_WhenFeedSizeExceedsLimit_ShouldTrimOldPosts() {
        String feedKey = "feed:1001";

        when(zSetOperations.size(feedKey)).thenReturn(15L);

        feedService.addPostToFeed(postEvent);

        verify(zSetOperations).add(feedKey, postEvent.getPostId(), postEvent.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        verify(zSetOperations).removeRange(feedKey, 0, 15 - maxFeedSize - 1);
    }
}
