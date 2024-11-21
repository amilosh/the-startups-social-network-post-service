package faang.school.postservice.producer;

import faang.school.postservice.dto.event.post.PostCreateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostCreateEvent> {

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic postsTopic) {
        super(kafkaTemplate, postsTopic);
    }

    @Override
    public void sendEvent(PostCreateEvent event) {
        super.sendEvent(event);
    }
}
