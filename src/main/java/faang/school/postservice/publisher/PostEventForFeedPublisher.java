package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventForFeedPublisher implements EventPublisher<FeedEventProto.FeedEvent> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Value("${spring.kafka.topics.post-for-feed.name}")
    private String topicName;

    @Override
    public void publish(FeedEventProto.FeedEvent event) {
        byte[] byteEvent = event.toByteArray();
        kafkaTemplate.send(topicName, byteEvent);
    }
}
