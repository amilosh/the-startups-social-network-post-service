package faang.school.postservice.producer;

import org.apache.kafka.clients.producer.RecordMetadata;

public interface EventProducer<T> {
    void sendEvent(T event);

    void handleSuccess(RecordMetadata metadata);

    void handleFailure(Throwable throwable);
}
