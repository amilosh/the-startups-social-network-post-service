package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    private final Class<T> eventType;

    protected AbstractKafkaConsumer(Class<T> eventType) {
        this.eventType = eventType;
    }

    @KafkaListener(topics = "#{__listener.getTopic()}", groupId = "#{__listener.getGroupId()}")
    public void consume(String message, Acknowledgment acknowledgment) {
        try {
            T event = parseEvent(message);
            processEvent(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event of type {}: {}", eventType.getSimpleName(), e.getMessage(), e);
        }
    }

    private T parseEvent(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(message, eventType);
    }

    protected abstract void processEvent(T event);

    protected abstract String getTopic();

    protected abstract String getGroupId();
}
