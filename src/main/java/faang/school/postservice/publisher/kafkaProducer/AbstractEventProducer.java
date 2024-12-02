package faang.school.postservice.publisher.kafkaProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;

    public void sendEvent(T event) {
        kafkaTemplate.send(topic.name(), event);
        log.info("send event: {} in topic {}", event, topic);
    }
}
