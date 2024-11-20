package faang.school.postservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    protected AbstractKafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected abstract String getTopic();

    public void sendEvent(T event) {
        kafkaTemplate.send(getTopic(), event);
    }
}
