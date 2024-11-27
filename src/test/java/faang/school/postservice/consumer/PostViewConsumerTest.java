package faang.school.postservice.consumer;

import faang.school.postservice.model.entity.redis.PostCache;
import faang.school.postservice.model.event.newsfeed.PostViewEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.RedissonLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.support.Acknowledgment;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostViewConsumerTest {
    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RedissonLock redissonLock;

    @InjectMocks
    private PostViewConsumer postViewConsumer;

    @Test
    void testConsumeSuccess() throws InterruptedException {
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
        doReturn(redissonLock).when(redissonClient).getLock(anyString());
        when(redissonLock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(redissonLock.isHeldByCurrentThread()).thenReturn(true);

        Acknowledgment acknowledgment = () -> {};
        postViewConsumer.consume(viewEvent, acknowledgment);
        verify(redissonClient).getLock(anyString());
        verify(redisPostRepository).findById(anyLong());
        verify(redissonLock).tryLock(anyLong(), any(TimeUnit.class));
        verify(redissonLock).isHeldByCurrentThread();
        verify(redissonLock).unlock();
    }

}