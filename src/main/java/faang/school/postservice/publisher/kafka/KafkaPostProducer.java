package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostNFEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements KafkaPublisher<PostNFEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postForNewsFeedTopic;

    @Override
    public void publish(PostNFEvent event) {
        kafkaTemplate.send(postForNewsFeedTopic.name(), event);
        log.info("PostNFEvent was sent with kafka");
    }
}
