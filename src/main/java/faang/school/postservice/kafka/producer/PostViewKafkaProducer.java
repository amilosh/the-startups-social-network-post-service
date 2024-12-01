package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostViewKafkaProducer extends AbstractKafkaProducer<PostViewKafkaEvent> {

    @Value("${kafka.topics.post-view}")
    private String postViewKafkaTopic;

    public PostViewKafkaProducer(KafkaTemplate<String, PostViewKafkaEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return postViewKafkaTopic;
    }
}

