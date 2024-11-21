package faang.school.postservice.producer;

import faang.school.postservice.dto.event.post.PostCreateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {

    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private NewTopic postsTopic;

    private static final String POSTS_TOPIC = "PostsTopic";

    @Test
    @DisplayName("Success when send PostCreateEvent in postsTopic")
    public void whenPublishEventShouldSuccess() {
        PostCreateEvent event = PostCreateEvent.builder().build();
        when(postsTopic.name()).thenReturn(POSTS_TOPIC);

        kafkaPostProducer.sendEvent(event);

        verify(kafkaTemplate).send(POSTS_TOPIC, event);
    }
}