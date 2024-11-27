package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.CommentCache;
import faang.school.postservice.mapper.comment.CommentCacheMapper;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.message.HeatFeedBatchMessage;
import faang.school.postservice.message.HeatFeedUserMessage;
import faang.school.postservice.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaHeatFeedConsumer {

    private final ObjectMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic.heat-feed-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Received comment publish message: {}", message);

            HeatFeedBatchMessage heatFeedBatchMessage = mapper.readValue(message, HeatFeedBatchMessage.class);
            List<HeatFeedUserMessage> heatFeedUsers = heatFeedBatchMessage.getMessages();
            heatFeedUsers.forEach(System.out::println);

            ack.acknowledge();

        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
