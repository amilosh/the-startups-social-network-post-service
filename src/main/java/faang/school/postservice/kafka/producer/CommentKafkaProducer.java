package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.kafka.CommentSentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentKafkaProducer extends AbstractKafkaProducer<CommentSentEvent> {

    @Value("${kafka.topics.comment}")
    private String commentTopic;

    public CommentKafkaProducer(KafkaTemplate<String, CommentSentEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopic() {
        return commentTopic;
    }
}

