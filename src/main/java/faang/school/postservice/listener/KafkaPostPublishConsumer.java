package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.PostPublishMessage;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostPublishConsumer {
    private final ObjectMapper mapper;
    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.post-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Received post publish message: {}", message);

            PostPublishMessage postPublishMessage = mapper.readValue(message, PostPublishMessage.class);
            List<Long> followerIds = postPublishMessage.getFollowerIds();
            if (!followerIds.isEmpty()) {
                Long postId = postPublishMessage.getPostId();
                LocalDateTime publishedAt = postPublishMessage.getPublishedAt();
                newsFeedService.allocateToFeeds(postId, publishedAt, followerIds);
            }
            ack.acknowledge();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
