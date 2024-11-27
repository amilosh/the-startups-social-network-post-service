package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.LikePublishMessage;
import faang.school.postservice.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final ObjectMapper mapper;
    private final PostRedisRepository postRedisRepository;

    @KafkaListener(topics = "${spring.kafka.topic.like-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Received like publish message: {}", message);

            LikePublishMessage likePublishMessage = mapper.readValue(message, LikePublishMessage.class);
            Long postId = likePublishMessage.getPostId();
            postRedisRepository.addLikeToPost(postId);

            ack.acknowledge();

        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
