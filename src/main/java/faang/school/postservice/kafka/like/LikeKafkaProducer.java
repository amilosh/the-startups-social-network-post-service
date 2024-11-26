package faang.school.postservice.kafka.like;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.like.event.CommentLikedKafkaEvent;
import faang.school.postservice.kafka.like.event.PostLikedKafkaEvent;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeKafkaProducer {
    @Value("${kafka.topic.post-liked-topic}")
    private String postLikedTopic;

    @Value("${kafka.topic.comment-liked-topic}")
    private String commentLikedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostLikeToKafka(Like like, LikeAction action) {
        PostLikedKafkaEvent postLikedKafkaEvent = build(like, action);
        kafkaTemplate.send(postLikedTopic, postLikedKafkaEvent);
    }

    public void sendPostLikeToKafka(Post post, LikeAction action) {
        PostLikedKafkaEvent postLikedKafkaEvent = build(post, action);
        kafkaTemplate.send(postLikedTopic, postLikedKafkaEvent);
    }

    public void sendCommentLikeToKafka(Like like, LikeAction likeAction) {
        CommentLikedKafkaEvent commentLikedKafkaEvent = mapToCommentLikeKafkaDto(like, likeAction);
        kafkaTemplate.send(commentLikedTopic, commentLikedKafkaEvent);
    }

    public void sendCommentLikeToKafka(Comment comment, LikeAction likeAction) {
        CommentLikedKafkaEvent commentLikedKafkaEvent = mapToCommentLikeKafkaDto(comment, likeAction);
        kafkaTemplate.send(commentLikedTopic, commentLikedKafkaEvent);
    }

    private PostLikedKafkaEvent build(Like like, LikeAction action) {
        return PostLikedKafkaEvent.builder()
                .postId(like.getPost().getId())
                .action(action)
                .build();
    }

    private PostLikedKafkaEvent build(Post post, LikeAction action) {
        return PostLikedKafkaEvent.builder()
                .postId(post.getId())
                .action(action)
                .build();
    }

    private CommentLikedKafkaEvent mapToCommentLikeKafkaDto(Like like, LikeAction likeAction) {
        return CommentLikedKafkaEvent.builder()
                .postId(like.getComment().getPost().getId())
                .commentId(like.getComment().getId())
                .action(likeAction)
                .build();
    }

    private CommentLikedKafkaEvent mapToCommentLikeKafkaDto(Comment comment, LikeAction likeAction) {
        return CommentLikedKafkaEvent.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .action(likeAction)
                .build();
    }
}
