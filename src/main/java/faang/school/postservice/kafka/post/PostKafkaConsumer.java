package faang.school.postservice.kafka.post;

import faang.school.postservice.kafka.post.event.PostPublishedKafkaEvent;
import faang.school.postservice.kafka.post.event.PostViewedKafkaEvent;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostKafkaConsumer {
    private final FeedService feedService;
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.post-published-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handlePostPublishedEvent(PostPublishedKafkaEvent postPublishedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            feedService.addFeed(postPublishedKafkaEvent);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post with id {} is not added to feed.", postPublishedKafkaEvent.getPostId());
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.post-viewed-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handlePostViewedEvent(PostViewedKafkaEvent postViewedKafkaEvent, Acknowledgment acknowledgment) {
        try {
            postService.incrementView(postViewedKafkaEvent.getPostId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post View with id {} is not added ???", postViewedKafkaEvent.getPostId());
            throw e;
        }
    }
}
