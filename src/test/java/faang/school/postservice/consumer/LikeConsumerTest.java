package faang.school.postservice.consumer;

import faang.school.postservice.model.entity.redis.PostCache;
import faang.school.postservice.model.event.newsfeed.LikeNewsFeedEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeConsumerTest {
    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    LikeConsumer likeConsumer;

    @Test
    void testConsumeSuccess() {
        var likeEvent = LikeNewsFeedEvent.builder()
                .postId(1)
                .authorID(2)
                .build();
        var postCache = PostCache.builder()
                .id(1L)
                .likes(0L)
                .title("test title")
                .content("test content")
                .views(0L)
                .likes(0L)
                .authorId(1L)
                .comments(new LinkedHashSet<>())
                .build();

        doReturn(Optional.of(postCache)).when(redisPostRepository).findById(anyLong());
        Acknowledgment acknowledgment = () -> {};
        likeConsumer.consume(likeEvent, acknowledgment);
        verify(redisPostRepository).findById(anyLong());
        verify(redisTemplate).watch(anyString());
        verify(redisTemplate).unwatch();
    }
}