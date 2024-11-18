package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostObservationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer implements KafkaPublisher<PostObservationEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postObservedTopic;

    @Override
    public void publish(PostObservationEvent event) {
        kafkaTemplate.send(postObservedTopic.name(), event);
        log.info("PostObservationEvent was sent");
    }
}
