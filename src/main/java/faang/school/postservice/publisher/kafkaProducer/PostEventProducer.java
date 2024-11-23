package faang.school.postservice.publisher.kafkaProducer;

import faang.school.postservice.dto.post.message.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventProducer extends AbstractEventProducer<PostEvent> {

    public PostEventProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic postTopic) {
        super(kafkaTemplate, postTopic);
    }

    public void sendEvent(PostEvent event) {
        super.sendEvent(event);
    }
}
