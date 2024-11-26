package faang.school.postservice.kafka.comment;

import faang.school.postservice.kafka.comment.event.CommentCreatedKafkaEvent;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentKafkaConsumer {
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.comment-created-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(CommentCreatedKafkaEvent dto, Acknowledgment acknowledgment) {
        try {
            postService.addCommentToPost(dto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Comment {} is not added to Post {}.", dto.getCommentId(), dto.getPostId());
            throw e;
        }
    }
}
