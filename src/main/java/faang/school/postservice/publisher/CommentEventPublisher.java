package faang.school.postservice.publisher;

import faang.school.postservice.model.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic commentPublishTopic;

    public void publish(CommentEvent commentEvent) {
        kafkaTemplate.send(commentPublishTopic.name(), commentEvent);
        log.info("Comment event was sent: {}", commentEvent);
    }
}
