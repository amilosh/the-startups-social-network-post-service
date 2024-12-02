package faang.school.postservice.kafka.producer;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeKafkaProducer extends AbstractKafkaProducer<LikeKafkaEvent> {

    @Value("${kafka.topics.like}")
    private String likeTopic;

    public LikeKafkaProducer(KafkaTemplate<String, LikeKafkaEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return likeTopic;
    }
}
