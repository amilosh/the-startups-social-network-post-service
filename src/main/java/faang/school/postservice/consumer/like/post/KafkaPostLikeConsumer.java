package faang.school.postservice.consumer.like.post;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.like.PostLikeKafkaEvent;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostLikeConsumer {

    private final PostCacheRepositoryImpl postCacheRepository;
    private final RedissonClient redissonClient;
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
        String lockKey = "lock:Post:" + postId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                try {
                    postCacheRepository.incrementLikesCount(postId);
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
