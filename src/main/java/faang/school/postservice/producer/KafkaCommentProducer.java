package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.comment.CommentCacheMapper;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer implements KafkaMessageProducer<Comment> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final CommentCacheMapper commentCacheMapper;

    @Value("${spring.kafka.topic.comment-publisher}")
    private String topic;

    @Override
    public void publish(Comment comment) {
        try {
            CommentPublishMessage commentMessage = commentCacheMapper.toCommentPublishMessage(comment);
            String message = objectMapper.writeValueAsString(commentMessage);
            kafkaTemplate.send(topic, message);
            log.info("Send message: {}", commentMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to json");
        }
    }


}
