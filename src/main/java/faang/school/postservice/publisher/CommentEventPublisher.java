package faang.school.postservice.publisher;

import faang.school.postservice.model.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final NewTopic commentTopic;
    private final KafkaTemplate<String, Object> redisTemplate;

    public void publish(CommentEvent commentEvent) {
        redisTemplate.send(commentTopic.name(), commentEvent);
    }
}
