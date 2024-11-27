package faang.school.postservice.consumer;

import faang.school.postservice.model.event.newsfeed.LikeNewsFeedEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeConsumer {
    private final RedisPostRepository redisPostRepository;
    private final RedissonClient redissonClient;

    @KafkaListener(topics = "${spring.data.kafka.channels.like-nf-channel}", groupId = "${spring.data.kafka.group}", concurrency = "2")
    public void consume(LikeNewsFeedEvent event, Acknowledgment ack) {
        RLock lock = redissonClient.getLock("post-lock-" + event.postId());
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                redisPostRepository.findById(event.postId()).ifPresent(post -> {
                    post.setLikes(post.getLikes() + 1);
                    redisPostRepository.save(post);
                });
            } else {
                log.error("Failed to acquire Redis lock");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted exception occurred", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        ack.acknowledge();
    }
}
