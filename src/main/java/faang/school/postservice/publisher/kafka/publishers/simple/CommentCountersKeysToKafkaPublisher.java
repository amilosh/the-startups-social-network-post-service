package faang.school.postservice.publisher.kafka.publishers.simple;

import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentCountersKeysToKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.post.update_comments}")
    private String topicName;

    public void publish(CommentCountersKeysMessage message) {
        kafkaTemplate.send(topicName, message);
    }
}
