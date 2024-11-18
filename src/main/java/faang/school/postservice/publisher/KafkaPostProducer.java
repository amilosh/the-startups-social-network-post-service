package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.mapper.post.PostPublishedEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements EventPublisher<PostPublishedEvent> {

    @Value("${spring.kafka.topics.posts.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final PostPublishedEventMapper postPublishedEventMapper;

    @Override
    public void publish(PostPublishedEvent event) {
        kafkaTemplate.send(topic, postPublishedEventMapper.toProto(event).toByteArray());
    }
}
