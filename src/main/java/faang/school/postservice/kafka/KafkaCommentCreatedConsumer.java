package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.CommentCreatedKafkaDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommentCreatedConsumer {
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.comment-created-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(CommentCreatedKafkaDto dto, Acknowledgment acknowledgment) {
        try {
            postService.addCommentToPost(dto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Comment {} is not added to Post {}.", dto.getCommentId(), dto.getPostId());
            throw e;
        }
    }
}
