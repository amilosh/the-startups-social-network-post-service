package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.ViewPublishMessage;
import faang.school.postservice.model.ViewEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class KafkaViewProducer extends AbstractKafkaProducer<ViewEntity> {

    @Value("${spring.kafka.topic.view-publisher}")
    private String topic;

    public KafkaViewProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected List<Object> createMessages(ViewEntity viewEntity) {
        return List.of(ViewPublishMessage.builder()
                .postId(viewEntity.getPost().getId())
                .viewCount(viewEntity.getViewCount())
                .build());
    }
}

