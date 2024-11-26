package faang.school.postservice.publisher;

import faang.school.postservice.model.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postPublishTopic;

    public void publish(PostEvent postEvent) {
        kafkaTemplate.send(postPublishTopic.name(), postEvent);
        log.info("Published post event: {}", postEvent);
    }
}
