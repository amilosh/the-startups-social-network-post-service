package faang.school.postservice.publisher.kafka.publishers.simple;

import faang.school.postservice.dto.post.message.NewPostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PartitionSubscribersToKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.post.update_feeds}")
    private String topicName;

    public void publish(NewPostMessage message) {

        kafkaTemplate.send(topicName, message);
    }
}
