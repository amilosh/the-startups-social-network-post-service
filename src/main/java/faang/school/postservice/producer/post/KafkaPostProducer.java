package faang.school.postservice.producer.post;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.post.PostCreateEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostCreateEvent> {

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        super(kafkaTemplate, kafkaProperties.getTopics().getPostCreatedTopic().getName(), kafkaProperties);
    }
}
