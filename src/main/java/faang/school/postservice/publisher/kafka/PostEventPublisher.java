package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventPublisher{
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postPublishTopic;

    public void publish(PostEvent event) {
        kafkaTemplate.send(postPublishTopic.name(), event);
    }
}
