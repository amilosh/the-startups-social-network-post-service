package faang.school.postservice.publisher;

import faang.school.postservice.model.event.AdBoughtEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdBoughtEventPublisher {
    private final NewTopic adBoughtEventTopic;
    private final KafkaTemplate<String, Object> redisTemplate;

    public void publish(AdBoughtEvent adBoughtEvent) {
        redisTemplate.send(adBoughtEventTopic.name(), adBoughtEvent);
        log.info("Ad bought event was sent");
    }
}
