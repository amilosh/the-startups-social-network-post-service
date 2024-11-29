package faang.school.postservice.consumer;

import faang.school.postservice.model.event.kafka.PostCommentEvent;
import faang.school.postservice.service.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer implements KafkaListenerBuilder<PostCommentEvent> {
    private final PostRedisService postRedisService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.channels.comment-channel}", groupId = "${spring.data.kafka.group}")
    public void processEvent(PostCommentEvent event, Acknowledgment acknowledgment) {
        log.info("Kafka comment event listener received comment for post {}", event.postId());
        postRedisService.addComment(event);
        log.info("Kafka comment event listener finished for post {}", event.postId());
        acknowledgment.acknowledge();
    }
}
