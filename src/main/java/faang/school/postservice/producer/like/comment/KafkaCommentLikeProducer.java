package faang.school.postservice.producer.like.comment;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.like.CommentLikeKafkaEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentLikeProducer extends AbstractEventProducer<CommentLikeKafkaEvent> {

    public KafkaCommentLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        super(kafkaTemplate, kafkaProperties.getTopics().getCommentLikeTopic().getName(), kafkaProperties);
    }
}
