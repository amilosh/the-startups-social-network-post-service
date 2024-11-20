package faang.school.postservice.consumer;

public interface KafkaConsumer<T> {
    void processEvent(T message);
}
