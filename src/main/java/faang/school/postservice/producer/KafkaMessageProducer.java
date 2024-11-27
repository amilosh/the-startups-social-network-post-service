package faang.school.postservice.producer;

public interface KafkaMessageProducer<T> {
    void publish(T object);
}
