package faang.school.postservice.listener;

import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.repository.redis.LikeRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private final LikeRedisRepository likeRedisRepository;

    @KafkaListener(topics = "like_channel", groupId = "like_event")
    public void onLikeEvent(LikeEvent likeEvent) {
        likeRedisRepository.addLikePost(likeEvent);
        log.info("Kafka like event received: {}", likeEvent);
    }
}
