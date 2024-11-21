package faang.school.postservice.producer.like;

public interface KafkaMessageProducer<T> {

    void sendMessage(T message);
}
