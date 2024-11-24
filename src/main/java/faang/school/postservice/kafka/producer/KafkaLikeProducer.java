package faang.school.postservice.kafka.producer;
import faang.school.postservice.model.event.kafka.KafkaLikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaLikeProducer extends AbstractKafkaProducer<KafkaLikeEvent> {

    @Value("${kafka.topics.like}")
    private String likeTopic;

    public KafkaLikeProducer(KafkaTemplate<String, KafkaLikeEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return likeTopic;
    }
}
