package faang.school.postservice.publisher;

import faang.school.postservice.model.event.LikeEvent;
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
class LikeEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private NewTopic likePublishTopic;

    @InjectMocks
    private LikeEventPublisher publisher;

    @Test
    @DisplayName("Publish Like Event Test")
    void testPublish() {
        var likeEvent = LikeEvent.builder().build();
        publisher.publish(likeEvent);
        verify(kafkaTemplate).send(likePublishTopic.name(), likeEvent);
        verifyNoMoreInteractions(kafkaTemplate);
    }
}
