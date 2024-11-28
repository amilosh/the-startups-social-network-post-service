package faang.school.postservice.consumer;

import faang.school.postservice.model.event.kafka.PostLikeEvent;
import faang.school.postservice.service.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer implements KafkaListenerBuilder<PostLikeEvent> {
    private final PostRedisService postRedisService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.channels.post-like}", groupId = "${spring.data.kafka.group}")
    public void processEvent(PostLikeEvent event, Acknowledgment acknowledgment) {
        log.info("Kafka Like event listener received like for post {}", event.postId());
        postRedisService.saveLikeOnPost(event);
        log.info("Kafka Like event listener finished for post {}", event.postId());
        acknowledgment.acknowledge();

    }
}
