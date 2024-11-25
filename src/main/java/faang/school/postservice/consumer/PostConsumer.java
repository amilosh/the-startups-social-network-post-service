package faang.school.postservice.consumer;

import faang.school.postservice.model.event.newsfeed.PostNewsFeedEvent;
import faang.school.postservice.service.impl.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.channels.post-channel}", groupId = "${spring.data.kafka.group}")
    public void consume(PostNewsFeedEvent event, Acknowledgment ack) {
        event.subscribers().forEach(subscriber ->
                feedService.bindPostToFollower(subscriber, event.postId()));
        ack.acknowledge();
    }
}
