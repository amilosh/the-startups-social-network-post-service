package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;

    public void sendEvent(T event) {
        kafkaTemplate.send(topic.name(), event);
        log.info("Sending event: {} in topic {}", event, topic);
        kafkaTemplate.send(topic.name(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send event! ", ex);
                    } else {
                        if (result != null) {
                            log.debug("Event sent successfully to topic {} with offset {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().offset());
                        }
                    }
                });
    }
}