package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.ViewPublishMessage;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaViewConsumer {

    private final ObjectMapper mapper;
    private final PostRedisRepository postRedisRepository;
    private final ViewService viewService;

    @KafkaListener(topics = "${spring.kafka.topic.view-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Received view publish message: {}", message);

            ViewPublishMessage viewPublishMessage = mapper.readValue(message, ViewPublishMessage.class);
            Long postId = viewPublishMessage.getPostId();
            Long viewCount = viewPublishMessage.getViewCount();
            postRedisRepository.addViewToPost(postId, viewCount);
            viewService.upsertView(postId, viewCount);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}