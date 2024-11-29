package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.message.PostEvent;
import faang.school.postservice.service.newsFeed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostConsumer {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @KafkaListener(
            topics = "${spring.kafka.topic.post.name}",
            concurrency = "${spring.kafka.consumer.concurrency}")
    public void listenEventPost(String message, Acknowledgment acknowledgment) {
        try {
            PostEvent postEvent = objectMapper.readValue(message, PostEvent.class);
            feedService.addPostToFeed(postEvent);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Message processing error: {}", message, e);
        }
    }
}
