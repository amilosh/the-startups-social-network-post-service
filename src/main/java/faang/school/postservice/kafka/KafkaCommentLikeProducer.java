package faang.school.postservice.kafka;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.dto.CommentLikeKafkaDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaCommentLikeProducer {
    @Value("${kafka.topic.comment-liked-topic}")
    private String commentLikedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommentLikeToKafka(Like like, LikeAction likeAction) {
        CommentLikeKafkaDto commentLikeKafkaDto = build(like, likeAction);
        kafkaTemplate.send(commentLikedTopic, commentLikeKafkaDto);
    }

    public void sendCommentLikeToKafka(Comment comment, LikeAction likeAction) {
        CommentLikeKafkaDto commentLikeKafkaDto = build(comment, likeAction);
        kafkaTemplate.send(commentLikedTopic, commentLikeKafkaDto);
    }

    private CommentLikeKafkaDto build(Like like, LikeAction likeAction) {
        return CommentLikeKafkaDto.builder()
                .postId(like.getComment().getPost().getId())
                .commentId(like.getComment().getId())
                .action(likeAction)
                .build();
    }

    private CommentLikeKafkaDto build(Comment comment, LikeAction likeAction) {
        return CommentLikeKafkaDto.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .action(likeAction)
                .build();
    }
}
