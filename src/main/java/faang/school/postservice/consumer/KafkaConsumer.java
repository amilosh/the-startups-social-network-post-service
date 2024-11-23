package faang.school.postservice.consumer;

public interface KafkaConsumer<T> {

    void listen(T event);
}
