package faang.school.postservice.consumer;

import faang.school.postservice.dto.cache.feed.FeedCacheDto;
import faang.school.postservice.dto.event.post.PostCreatedEvent;
import faang.school.postservice.repository.cache.FeedCacheRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private boolean allProcessedSuccessfully = true;
    private final FeedCacheRepositoryImpl feedCacheRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.postsTopic.name}",
            groupId = "${spring.data.kafka.consumerConfig.groupId}")
    public void listenPostEvent(PostCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("start listenPostEvent with: {}", event);

        List<Long> subscribersIds = event.getSubscribers();

        for (Long subscriberId : subscribersIds) {
            try {
                FeedCacheDto feedCacheDto = feedCacheRepository.findBySubscriberId(subscriberId)
                        .orElse(buildFeedCacheDto(subscriberId));
                log.info("feedCacheDto - {}", feedCacheDto.toString());

                feedCacheRepository.addPostId(feedCacheDto, event.getPostId());

                feedCacheRepository.save(feedCacheDto);
            } catch (Exception ex) {
                log.error("Error processing subscriberId: {}", subscriberId, ex);
                allProcessedSuccessfully = false;
            }
        }

        if (allProcessedSuccessfully) {
            acknowledgment.acknowledge();
            log.info("successfully finish listenPostEvent");
        } else {
            log.warn("Not all subscribers processed successfully, ack not sent");
        }
    }

    private FeedCacheDto buildFeedCacheDto(Long subscriberId) {
        return FeedCacheDto.builder()
                .subscriberId(subscriberId)
                .postsIds(new TreeSet<>(Comparator.reverseOrder()))
                .build();
    }
}
