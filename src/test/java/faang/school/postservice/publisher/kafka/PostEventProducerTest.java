package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.post.message.PostEvent;
import faang.school.postservice.publisher.kafkaProducer.PostEventProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostEventProducerTest {

    PostEventProducer postEventProducer;
    NewTopic postTopic;
    KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        postTopic = Mockito.mock(NewTopic.class);
        postEventProducer = new PostEventProducer(kafkaTemplate, postTopic);
    }

    @Test
    void testSendEvent_successfulCompletion() {
        PostEvent event = Mockito.mock(PostEvent.class);
        when(postTopic.name()).thenReturn("post-topic");
        when(kafkaTemplate.send("post-topic", event)).thenReturn(null);

        postEventProducer.sendEvent(event);

        verify(kafkaTemplate).send("post-topic", event);
    }
}
