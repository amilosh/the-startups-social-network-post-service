package faang.school.postservice.producer.like.comment;

import faang.school.postservice.event.kafka.comment.like.CommentLikeKafkaEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentLikeProducer extends AbstractEventProducer<CommentLikeKafkaEvent> {

    public KafkaCommentLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic commentLikeTopic) {
        super(kafkaTemplate, commentLikeTopic);
    }
}
