package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.PostEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostConsumer {
    @KafkaListener(topics ="${spring.data.kafka.topics.posts}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listen(PostEvent postEvent) {
        log.info("Post event received with author ID: {}", postEvent.getAuthorId());
    }

}
