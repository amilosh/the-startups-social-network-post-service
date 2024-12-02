package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.HeatFeedUserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KafkaHeatFeedProducer extends AbstractKafkaProducer<List<UserDto>> {

    @Value("${spring.kafka.topic.heat-feed-publisher}")
    private String topic;

    public KafkaHeatFeedProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected List<Object> createMessages(List<UserDto> users) {
        return users.stream()
                .map(user -> HeatFeedUserMessage.builder()
                        .userId(user.getId())
                        .followingIds(user.getFollowingsIds())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
