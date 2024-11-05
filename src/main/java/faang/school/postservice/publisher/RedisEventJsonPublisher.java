package faang.school.postservice.publisher;

public interface RedisEventJsonPublisher<T> {
    void publish(T event);
}
