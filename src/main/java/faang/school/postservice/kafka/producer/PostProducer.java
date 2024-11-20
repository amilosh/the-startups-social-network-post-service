package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.kafka.PostCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostProducer extends AbstractKafkaProducer<PostCreatedEvent> {

    @Value("${kafka.topics.post}")
    private String postTopic;

    public PostProducer(KafkaTemplate<String, PostCreatedEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return postTopic;
    }
}

