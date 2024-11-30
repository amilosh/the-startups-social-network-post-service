package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.dto.KafkaLikeDto;
import faang.school.postservice.model.event.kafka.KafkaLikeEvent;
import faang.school.postservice.service.impl.FeedServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer extends AbstractKafkaConsumer<KafkaLikeEvent> {

    private final FeedServiceImpl feedService;

    @KafkaListener(topics = "${kafka.topics.like}", groupId = "post-service")
    @Override
    protected void processEvent(KafkaLikeEvent event) {
        KafkaLikeDto likeDto = event.getKafkaLikeDto();
        Long postId = likeDto.getPostId();
        log.info("Processing like event for post: {}", postId);

        try {
            feedService.incrementLikeCount(postId);
            log.info("Successfully incremented like count for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Error incrementing like count for post ID {}: {}", postId, e.getMessage());
        }
    }
}
