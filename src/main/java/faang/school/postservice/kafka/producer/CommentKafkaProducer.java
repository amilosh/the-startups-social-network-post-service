package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentKafkaProducer extends AbstractKafkaProducer<CommentSentKafkaEvent> {

    @Value("${kafka.topics.comment}")
    private String commentKafkaTopic;

    public CommentKafkaProducer(KafkaTemplate<String, CommentSentKafkaEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return commentKafkaTopic;
    }
}

