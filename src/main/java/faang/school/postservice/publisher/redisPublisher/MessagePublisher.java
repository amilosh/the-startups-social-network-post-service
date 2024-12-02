package faang.school.postservice.publisher.redisPublisher;

public interface MessagePublisher<T> {
    void publish(T message);
}
