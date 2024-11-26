package faang.school.postservice.publisher;

import faang.school.postservice.model.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic likePublishTopic;

    public void publish(LikeEvent likeEvent) {
        kafkaTemplate.send(likePublishTopic.name(), likeEvent);
        log.info("Published like event: {}", likeEvent);
    }
}
