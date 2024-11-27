package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.HeatFeedBatchMessage;
import faang.school.postservice.message.HeatFeedUserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHeatFeedProducer implements KafkaMessageProducer<List<UserDto>> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.heat-heap-publisher}")
    private String topic;

    @Override
    public void publish(List<UserDto> users) {
        try {
            List<HeatFeedUserMessage> batch = users.stream()
                    .map(user -> HeatFeedUserMessage.builder()
                            .userId(user.getId())
                            .followingIds(user.getFollowingsIds())
                            .build())
                    .toList();
            HeatFeedBatchMessage messageBatch = new HeatFeedBatchMessage(batch);
            String message = objectMapper.writeValueAsString(messageBatch);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to json");
        }
    }
}
