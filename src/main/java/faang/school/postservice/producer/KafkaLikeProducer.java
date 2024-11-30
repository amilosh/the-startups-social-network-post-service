package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.LikePublishMessage;
import faang.school.postservice.model.Like;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractKafkaProducer<Like> {

    @Value("${spring.kafka.topic.like-publisher}")
    private String topic;

    public KafkaLikeProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected Object createMessage(Like like) {
        return LikePublishMessage.builder()
                .postId(like.getPost().getId())
                .build();
    }
}




