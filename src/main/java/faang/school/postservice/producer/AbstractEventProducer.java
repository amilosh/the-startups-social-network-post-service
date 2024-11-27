package faang.school.postservice.producer;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> implements EventProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;
    private final KafkaProperties kafkaProperties;

    @Override
    public void sendEvent(T event) {
        kafkaTemplate.send(topicName, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        handleFailure(ex);
                    } else {
                        if (result != null) {
                            handleSuccess(result.getRecordMetadata());
                        }
                    }
                });
    }

    @Override
    public void handleSuccess(RecordMetadata metadata) {
        log.debug("Event sent successfully to topic {} with offset {}", metadata.topic(), metadata.offset());
    }

    @Override
    public void handleFailure(Throwable throwable) {
        log.error("Failed to send event! ", throwable);
    }
}