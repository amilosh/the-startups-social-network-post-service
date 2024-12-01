package faang.school.postservice.kafka.comment;

import faang.school.postservice.kafka.comment.event.CommentCreatedKafkaEvent;
import faang.school.postservice.model.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentKafkaProducer {
    @Value("${kafka.topic.comment-created-topic}")
    private String commentCreatedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommentToKafka(Comment comment) {
        CommentCreatedKafkaEvent commentCreatedKafkaEvent = build(comment);
        kafkaTemplate.send(commentCreatedTopic, commentCreatedKafkaEvent);
    }

    private CommentCreatedKafkaEvent build(Comment comment) {
        return CommentCreatedKafkaEvent.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .build();
    }
}
