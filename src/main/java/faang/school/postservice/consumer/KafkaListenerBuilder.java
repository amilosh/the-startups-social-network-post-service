package faang.school.postservice.consumer;

import org.springframework.kafka.support.Acknowledgment;

public interface KafkaListenerBuilder<T> {
    void processEvent(T event, Acknowledgment acknowledgment);
}
