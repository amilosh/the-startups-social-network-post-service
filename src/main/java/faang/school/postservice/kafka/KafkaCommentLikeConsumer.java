package faang.school.postservice.kafka;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.dto.CommentLikeKafkaDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommentLikeConsumer {
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.comment-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(CommentLikeKafkaDto commentLikeKafkaDto, Acknowledgment acknowledgment) {
        try {
            Long postId = commentLikeKafkaDto.getPostId();
            Long commentId = commentLikeKafkaDto.getCommentId();
            LikeAction action = commentLikeKafkaDto.getAction();
            postService.changeCommentLike(postId, commentId, action);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Like is not added to Comment with id {}.", commentLikeKafkaDto.getCommentId());
            throw e;
        }
    }
}
