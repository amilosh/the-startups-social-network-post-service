package faang.school.postservice.publisher;

import faang.school.postservice.model.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final NewTopic likeTopic;
    private final KafkaTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent likeEvent) {
        redisTemplate.send(likeTopic.name(), likeEvent);
    }
}
