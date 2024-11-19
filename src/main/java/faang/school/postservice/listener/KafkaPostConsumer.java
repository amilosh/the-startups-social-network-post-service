package faang.school.postservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.PostPublishMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic.post-publisher}")
    public void consume(String message, Acknowledgment ack) {
        log.info("Received post publish message: {}", message);

        PostPublishMessage postPublishMessage = mapper.convertValue(message, PostPublishMessage.class);


        ack.acknowledge();
    }
}
