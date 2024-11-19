package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.PostKafkaDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(
            topics = "${kafka.topic.post-published-topic}",
            groupId = "${kafka.consumer.group-id}")
    public void handle(PostKafkaDto postKafkaDto) {
        feedService.addFeed(postKafkaDto);
    }
}
