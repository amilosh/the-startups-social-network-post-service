package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.CommentCreatedKafkaDto;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaCommentProducer {
    @Value("${kafka.topic.comment-created-topic}")
    private String commentCreatedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommentToKafka(Comment comment) {
        CommentCreatedKafkaDto commentCreatedKafkaDto = build(comment);
        kafkaTemplate.send(commentCreatedTopic, commentCreatedKafkaDto);
    }

    private CommentCreatedKafkaDto build(Comment comment) {
        return CommentCreatedKafkaDto.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .build();
    }
}
