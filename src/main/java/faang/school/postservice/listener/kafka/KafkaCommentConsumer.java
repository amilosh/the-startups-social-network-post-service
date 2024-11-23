package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@KafkaListener(topics = "${spring.kafka.topics.comment}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaCommentConsumer {

    private final PostCacheService postCacheService;

    @KafkaHandler
    public void handleComment(CommentEventKafka event, Acknowledgment ack) {
        log.info("Starting processing of CommentEventKafka for Post ID: {}", event.getPostId());

        postCacheService.updatePostComments(event);

        log.info("Successfully processed CommentEventKafka for Post ID: {}", event.getPostId());

        ack.acknowledge();
    }

}
