package faang.school.postservice.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    protected abstract void processEvent(T event);

    public void consume(ConsumerRecord<String, T> record, Acknowledgment acknowledgment) {
        T event = record.value();
        log.info("Consumed event from topic {}: {}", record.topic(), event);
        processEvent(event);
    }
}
