package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    protected abstract String getTopic();

    protected abstract List<Object> createMessages(T data);

    public void publish(T data) {
        try {
            List<Object> objects = createMessages(data);
            for (Object object : objects) {
                String message = objectMapper.writeValueAsString(object);
                kafkaTemplate.send(getTopic(), message);
                log.info("Sent message to kafka Topic: {} Message: {}", getTopic(), message);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
        }
    }
}