package faang.school.postservice.kafka.like;

import faang.school.postservice.kafka.like.event.CommentLikedKafkaEvent;
import faang.school.postservice.kafka.like.event.PostLikedKafkaEvent;
import faang.school.postservice.kafka.post.event.PostViewedKafkaEvent;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.comment.redis.CommentRedisService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.redis.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserActionKafkaConsumer {
    private final PostService postService;
    private final PostRedisService postRedisService;
    private final CommentRedisService commentRedisService;
    private final CommentService commentService;

    @KafkaListener(
            topics = "${kafka.topic.post-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostLikedKafkaEvent postLikedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            Map<Long, Integer> postLikes = postLikedKafkaEvent.getPostLikes();
            postService.changeLikesAmountForPosts(postLikes);
            postRedisService.changeLikesAmountForPosts(postLikes);
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
            Map<Long, Integer> commentLikes = commentLikedKafkaEvent.getCommentLikes();
            commentService.changeLikesAmountForComments(commentLikes);
            commentRedisService.changeLikesAmountForComments(commentLikes);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Likes is not added to Comments.");
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.post-viewed-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handlePostViewedEvent(PostViewedKafkaEvent postViewedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            Map<Long, Integer> postViews = postViewedKafkaEvent.getPostViews();
            postService.changeViewsAmountForPosts(postViews);
            postRedisService.changeViewsAmountForPosts(postViews);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Likes is not added to Views.");
            throw e;
        }
    }
}
