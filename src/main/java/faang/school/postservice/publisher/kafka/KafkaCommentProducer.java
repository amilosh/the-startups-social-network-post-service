package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer implements KafkaPublisher<PostCommentEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic commentKafkaTopic;

    @Override
    public void publish(PostCommentEvent event) {
        kafkaTemplate.send(commentKafkaTopic.name(), event);
        log.info("comment event was sent with kafka");
    }
}
