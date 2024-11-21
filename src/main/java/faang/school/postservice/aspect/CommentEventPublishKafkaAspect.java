package faang.school.postservice.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublishKafkaAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value(value = "${spring.kafka.topic.comment-publisher}")
    private String publishCommentTopicName;

    @AfterReturning(pointcut = "@annotation(CommentEventPublishKafka)", returning = "comment")
    @Async("treadPool")
    public void publishCommentAdvice(Comment comment) {
        try {
            CommentPublishMessage commentPublishMessage = new CommentPublishMessage();
            commentPublishMessage.setPostId(comment.getPost().getId());
            commentPublishMessage.setCommentAuthorId(comment.getAuthorId());

            String message = objectMapper.writeValueAsString(commentPublishMessage);
            kafkaTemplate.send(publishCommentTopicName, message);

            log.info("Message published to kafka broker: topic = {}, message = {}", publishCommentTopicName, commentPublishMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
