package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.PostEventDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostKafkaConsumer implements KafkaConsumer<PostEventDto> {
    private final FeedService feedService;
    @Override
    @KafkaListener(
        id = "consumer-posts",
        topics = "${spring.data.kafka.topic.post-topic.name}",
        containerFactory = "kafkaPostConsumerFactory",
        groupId = "posts"
    )
    public void listen(PostEventDto event) {
        log.info("Received event: {}", event);
        feedService.process(event);
    }
}
