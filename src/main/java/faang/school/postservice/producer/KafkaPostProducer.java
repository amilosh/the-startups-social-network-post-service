package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer {

    @Value("${spring.data.kafka.topics.posts-channel}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostEvent(PostDto postDto) {
        kafkaTemplate.send(topic, postDto);
        log.info("Kafka sent an event post: " + postDto);
    }

}

