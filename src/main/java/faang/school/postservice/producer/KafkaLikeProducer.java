package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.LikePublishMessage;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeProducer implements KafkaMessageProducer<Like> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.like-publisher}")
    private String topic;

    @Override
    public void publish(Like like) {
        try {
            LikePublishMessage likeMessage = LikePublishMessage.builder()
                    .postId(like.getPost().getId())
                    .build();

            String message = objectMapper.writeValueAsString(likeMessage);
            kafkaTemplate.send(topic, message);
            log.info("Sent message to kafka Topic: {} Message: {}", topic, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to json");
        }
    }
}




