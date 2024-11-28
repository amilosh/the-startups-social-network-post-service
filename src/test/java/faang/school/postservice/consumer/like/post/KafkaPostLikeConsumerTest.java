package faang.school.postservice.consumer.like.post;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.like.PostLikeKafkaEvent;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaPostLikeConsumerTest {

    @Mock
    private PostCacheRepositoryImpl postCacheRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock rlock;
    @Mock
    private KafkaProperties kafkaProperties;
    @InjectMocks
    private KafkaPostLikeConsumer kafkaPostLikeConsumer;

    private PostLikeKafkaEvent postLikeKafkaEvent;

    @BeforeEach
    void setUp() {
        postLikeKafkaEvent = PostLikeKafkaEvent.builder()
                .postId(5L)
                .postAuthorId(10L)
                .likeAuthorId(3L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("When method called then handle given kafka event and increment postCacheDto likesCount field by one")
    public void whenPostIdGivenFromKafkaLikeEventThenIncrementLikesCountByOneInPostCacheDto()
            throws InterruptedException {
        String keyLock = "lock:Post:" + postLikeKafkaEvent.getPostId();
        when(redissonClient.getLock(keyLock)).thenReturn(rlock);
        when(rlock.tryLock(1, 5, TimeUnit.SECONDS)).thenReturn(true);
        kafkaPostLikeConsumer.likePostListener(postLikeKafkaEvent);
    }
}
