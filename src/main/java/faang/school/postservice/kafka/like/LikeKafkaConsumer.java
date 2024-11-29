package faang.school.postservice.kafka.like;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.like.event.CommentLikedKafkaEvent;
import faang.school.postservice.kafka.like.event.PostLikedKafkaEvent;
import faang.school.postservice.service.comment.redis.CommentRedisService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.redis.PostRedisService;
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
    private final PostRedisService postRedisService;
    private final CommentRedisService commentRedisService;

    @KafkaListener(
            topics = "${kafka.topic.post-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostLikedKafkaEvent postLikedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            postService.changeLikesAmountForPosts(postLikedKafkaEvent.getPostLikes());
            postRedisService.changeLikesAmountForPosts(postLikedKafkaEvent.getPostLikes());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Likes is not added to Posts.");
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.comment-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(CommentLikedKafkaEvent commentLikedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            Long commentId = commentLikedKafkaEvent.getCommentId();
            LikeAction likeAction = commentLikedKafkaEvent.getAction();
            commentRedisService.addOrRemoveLike(commentId, likeAction);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Like is not added to Comment with id {}.", commentLikedKafkaEvent.getCommentId());
            throw e;
        }
    }
}
