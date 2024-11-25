package faang.school.postservice.producer.like.post;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.post.like.PostLikeKafkaEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostLikeProducer extends AbstractEventProducer<PostLikeKafkaEvent> {

    public KafkaPostLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        super(kafkaTemplate, kafkaProperties.getTopics().getPostLikeTopic().getName(), kafkaProperties);
    }
}
