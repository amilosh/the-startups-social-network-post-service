package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentProducer extends KafkaEventProducer {

    public KafkaCommentProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper mapper,
                                @Value("${spring.kafka.topic.comment-publisher}") String commentPostTopic) {
        super(kafkaTemplate, commentPostTopic, mapper);
    }

    public void publishComment(Comment comment) {
        CommentPublishMessage commentPublishMessage = CommentPublishMessage.builder()
                .postId(comment.getPost().getId())
                .commentAuthorId(comment.getAuthorId())
                .build();

        publishEvent(commentPublishMessage);
    }
}