package faang.school.postservice.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.LikePublishMessage;
import faang.school.postservice.model.Like;
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
public class LikeEventPublishAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value(value = "${spring.kafka.topic.like-publisher}")
    private String publishLikeTopicName;

    @AfterReturning(pointcut = "@annotation(LikeEventPublishKafka)", returning = "like")
    @Async("treadPool")
    public void publishLikeAdvice(Like like) {
        try {
            LikePublishMessage likePublishMessage = new LikePublishMessage();
            likePublishMessage.setPostId(like.getPost().getId());
            likePublishMessage.setUserId(like.getUserId());

            String message = objectMapper.writeValueAsString(likePublishMessage);
            kafkaTemplate.send(publishLikeTopicName, message);

            log.info("Message published to kafka broker: topic = {}, message = {}", publishLikeTopicName, likePublishMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
