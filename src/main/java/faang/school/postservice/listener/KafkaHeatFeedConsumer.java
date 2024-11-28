package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.HeatFeedBatchMessage;
import faang.school.postservice.message.HeatFeedUserMessage;
import faang.school.postservice.service.NewsFeedService;
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
    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.heat-feed-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Received comment publish message: {}", message);

            HeatFeedBatchMessage heatFeedBatchMessage = mapper.readValue(message, HeatFeedBatchMessage.class);
            List<HeatFeedUserMessage> heatFeedUsers = heatFeedBatchMessage.getMessages();
            heatFeedUsers.forEach(heatFeedUser -> {
                Long userId = heatFeedUser.getUserId();
                List<Long> followingIds = heatFeedUser.getFollowingIds();
                newsFeedService.heatUserFeed(userId, followingIds);
            });

            ack.acknowledge();

        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
