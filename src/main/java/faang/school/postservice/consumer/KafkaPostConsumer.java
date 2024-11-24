package faang.school.postservice.consumer;

import faang.school.postservice.model.event.kafka.PostNFEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer implements KafkaListenerBuilder<PostNFEvent> {
    private final FeedService feedService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.channels.post-newsfeed}", id = "${spring.data.kafka.group}")
    public void processEvent(PostNFEvent post, Acknowledgment acknowledgment) {
        log.info("Kafka post event consumer received post with id {}", post.postId());
        post.followersId()
                .forEach(followerId -> feedService.bindPostToFollower(followerId, post.postId()));
        log.info("Kafka post event consumer finished for post with id {}", post.postId());
        acknowledgment.acknowledge();
    }
}
