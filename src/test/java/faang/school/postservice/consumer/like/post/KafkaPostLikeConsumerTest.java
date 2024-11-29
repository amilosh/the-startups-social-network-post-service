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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaPostLikeConsumerTest {

    @Mock
    private PostCacheRepositoryImpl postCacheRepository;
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
    public void whenPostIdGivenFromKafkaLikeEventThenIncrementLikesCountByOneInPostCacheDto() {
        when(postCacheRepository.incrementLikesCount(postLikeKafkaEvent.getPostId())).thenReturn(true);
        kafkaPostLikeConsumer.likePostListener(postLikeKafkaEvent);
        Mockito.verify(postCacheRepository).incrementLikesCount(postLikeKafkaEvent.getPostId());
    }
}
