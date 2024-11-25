package faang.school.postservice.publisher;

import faang.school.postservice.model.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic topic;

    @InjectMocks
    private CommentEventPublisher publisher;

    @Test
    @DisplayName("Publish Comment Event Test")
    void testPublish() {
        var commentEvent = CommentEvent.builder().build();
        publisher.publish(commentEvent);
        verify(redisTemplate).send(topic.name(), commentEvent);
        verifyNoMoreInteractions(redisTemplate);
    }
}