package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.dto.KafkaLikeDto;
import faang.school.postservice.model.event.kafka.KafkaLikeEvent;
import faang.school.postservice.service.RedisPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer extends AbstractKafkaConsumer<KafkaLikeEvent> {

    private final RedisPostService redisPostService;

    @Override
    public void consume(ConsumerRecord<String, KafkaLikeEvent> record, Acknowledgment acknowledgment) {
        super.consume(record, acknowledgment);

        try {
            KafkaLikeEvent event = record.value();
            processEvent(event);
            acknowledgment.acknowledge();
            log.info("Acknowledged processing of event for post ID: {}", event.getKafkaLikeDto().getPostId());
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.like}", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    protected void processEvent(KafkaLikeEvent event) {
        KafkaLikeDto likeDto = event.getKafkaLikeDto();
        Long likeId = likeDto.getId();
        Long postId = likeDto.getPostId();
        log.info("Processing like event for post: {}", postId);

        try {
            redisPostService.incrementLikesWithTransaction(postId, likeId);
            log.info("Successfully incremented like count for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Error incrementing like count for post ID {}: {}", postId, e.getMessage());
            throw e;
        }
    }
}

