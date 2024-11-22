package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.PostViewEventKafka;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${spring.kafka.topics.post-view}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaPostViewConsumer {

    private final PostCacheService postCacheService;

    @KafkaHandler
    public void handlePostView(PostViewEventKafka event) {
        log.info("Starting processing of PostViewEventKafka with Post ID: {}", event.getPostDto().getId());

        postCacheService.addPostView(event.getPostDto());

        log.info("Successfully processed PostViewEventKafka with Post ID: {}", event.getPostDto().getId());
    }
}
