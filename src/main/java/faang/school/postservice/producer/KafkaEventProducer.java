package faang.school.postservice.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@AllArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    protected String topicName;
    protected ObjectMapper mapper;

    public void publishEvent(Object message) {
        try {
            String messageString = mapper.writeValueAsString(message);
            kafkaTemplate.send(topicName, messageString);

            log.info("Message published to kafka broker: topic = {}, message = {}", topicName, messageString);
        } catch (JsonProcessingException e) {
            log.error("Error serializing message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize message for Kafka", e);
        }
    }
}
