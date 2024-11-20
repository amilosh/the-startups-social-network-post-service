package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.PostKafkaDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(
            topics = "${kafka.topic.post-published-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostKafkaDto postKafkaDto, Acknowledgment acknowledgment) {
        try {
            feedService.addFeed(postKafkaDto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post with id {} is not added to feed.", postKafkaDto.getPostId());
            throw e;
        }
    }
}
