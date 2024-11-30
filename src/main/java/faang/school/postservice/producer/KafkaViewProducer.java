package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.ViewPublishMessage;
import faang.school.postservice.model.ViewEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaViewProducer implements KafkaMessageProducer<ViewEntity> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Value("${spring.kafka.topic.view-publisher}")
    private String topic;

    public void publish(ViewEntity viewEntity) {
        try {
            ViewPublishMessage viewMessage = ViewPublishMessage.builder().
                    postId(viewEntity.getPost().getId())
                    .viewCount(viewEntity.getViewCount())
                    .build();

            String message = mapper.writeValueAsString(viewMessage);
            kafkaTemplate.send(topic, message);
            log.info("Sent message to kafka Topic: {} Message: {}", topic, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to json");
        }
    }
}

