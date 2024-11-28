package faang.school.postservice.consumer;

import faang.school.postservice.model.event.kafka.PostObservationEvent;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.service.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostViewConsumer implements KafkaListenerBuilder<PostObservationEvent>{
    private final PostRedisService postRedisService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.channels.post-observed}", groupId = "${spring.data.kafka.group}")
    public void processEvent(PostObservationEvent event, Acknowledgment acknowledgment) {
        log.info("Starting incrementing views for Post {}", event.postId());
        postRedisService.incrementViews(event);
        log.info("Post {} views were incremented", event.postId());
        acknowledgment.acknowledge();
    }
}
