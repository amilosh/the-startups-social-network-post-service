package faang.school.postservice.kafka.like;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.like.event.CommentLikedKafkaEvent;
import faang.school.postservice.kafka.like.event.PostLikedKafkaEvent;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LikeKafkaConsumer {
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.post-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostLikedKafkaEvent postLikedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            postService.changeLike(postLikedKafkaEvent.getPostId(), postLikedKafkaEvent.getAction());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Like is not added to Post with id {}.", postLikedKafkaEvent.getPostId());
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.comment-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(CommentLikedKafkaEvent commentLikedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            Long postId = commentLikedKafkaEvent.getPostId();
            Long commentId = commentLikedKafkaEvent.getCommentId();
            LikeAction action = commentLikedKafkaEvent.getAction();
            postService.changeCommentLike(postId, commentId, action);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Like is not added to Comment with id {}.", commentLikedKafkaEvent.getCommentId());
            throw e;
        }
    }
}
