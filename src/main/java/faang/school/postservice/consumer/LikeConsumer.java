package faang.school.postservice.consumer;

import faang.school.postservice.model.event.newsfeed.LikeNewsFeedEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeConsumer {
    private final RedisPostRepository redisPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "${spring.data.kafka.channels.like-nf-channel}", groupId = "${spring.data.kafka.group}")
    public void consume(LikeNewsFeedEvent event, Acknowledgment ack) {
        String key = "Post:%d".formatted(event.postId());
        redisPostRepository.findById(event.postId()).ifPresent(post -> {
            redisTemplate.watch(key);
            post.setLikes(post.getLikes() + 1);
            redisPostRepository.save(post);
            redisTemplate.unwatch();
        });
        ack.acknowledge();
    }
}
