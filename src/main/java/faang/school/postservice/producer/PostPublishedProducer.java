package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.PostPublishedKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostPublishedProducer extends AbstractEventProducer<PostPublishedKafkaEvent> {
    public PostPublishedProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic posts) {
        super(kafkaTemplate, posts);
    }
}
