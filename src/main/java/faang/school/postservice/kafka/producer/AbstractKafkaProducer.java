package faang.school.postservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    protected AbstractKafkaProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected abstract String getTopic();
//TODO переделать, чтобы отправлялась строка, т.е использовать objectMapper и поменять сериализатор/девериализатор
// у кафки в application.yaml (чтобы потом можно было через cmd слать json-ы как эвенты)
    public void sendEvent(T event) {
        kafkaTemplate.send(getTopic(), event);
    }
}
