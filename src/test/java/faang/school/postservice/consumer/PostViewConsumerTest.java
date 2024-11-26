package faang.school.postservice.consumer;

import faang.school.postservice.model.entity.redis.PostCache;
import faang.school.postservice.model.event.newsfeed.PostViewEvent;
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
class PostViewConsumerTest {
    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private PostViewConsumer postViewConsumer;

    @Test
    void testConsumeSuccess() {
        var viewEvent = new PostViewEvent(2);
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
        postViewConsumer.consume(viewEvent, acknowledgment);
        verify(redisPostRepository).findById(anyLong());
        verify(redisTemplate).watch(anyString());
        verify(redisTemplate).unwatch();
    }

}