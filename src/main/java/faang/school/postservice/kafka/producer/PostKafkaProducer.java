package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostKafkaProducer extends AbstractKafkaProducer<PostPublishedKafkaEvent> {

    @Value("${kafka.topics.post}")
    private String postKafkaTopic;

    public PostKafkaProducer(KafkaTemplate<String, PostPublishedKafkaEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return postKafkaTopic;
    }
}

