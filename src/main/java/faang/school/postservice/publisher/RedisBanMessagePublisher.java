package faang.school.postservice.publisher;

import faang.school.postservice.model.event.BanEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisBanMessagePublisher {
    private final KafkaTemplate<String, Object> redisTemplate;
    private final NewTopic userBanTopic;

    public void publish(BanEvent banEvent) {
        redisTemplate.send(userBanTopic.name(), banEvent);
    }
}
