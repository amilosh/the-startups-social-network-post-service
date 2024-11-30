package faang.school.postservice.consumer.like.post;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.like.PostLikeKafkaEvent;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostLikeConsumer {

    private final PostCacheRepositoryImpl postCacheRepository;
    private final KafkaProperties kafkaProperties;

    @KafkaListener(topics = "#{@kafkaProperties.topics.postLikeTopic.name}",
            groupId = "#{@kafkaProperties.consumerConfig.groupId}")
    public void likePostListener(PostLikeKafkaEvent event) {
        log.debug("Received like on post {} created at : {}", event.getPostId(), event.getCreatedAt());
        log.debug("Processing likes update...");
        boolean isProcessed = handlePostLikeEvent(event.getPostId());
        log.debug("Processed : {}", isProcessed);
    }

    private boolean handlePostLikeEvent(Long postId) {
        return postCacheRepository.incrementLikesCount(postId);
    }
}
