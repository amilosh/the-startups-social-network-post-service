package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer implements KafkaPublisher<PostLikeEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postLikeEventTopic;

    @Override
    public void publish(PostLikeEvent event) {
        kafkaTemplate.send(postLikeEventTopic.name(), event);
        log.info("Like event to post {} by {} was sent", event.postId(), event.authorId());
    }
}
