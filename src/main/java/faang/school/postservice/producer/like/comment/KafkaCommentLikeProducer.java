package faang.school.postservice.producer.like.comment;

import faang.school.postservice.event.kafka.comment.like.CommentLikeKafkaEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaCommentLikeProducer extends AbstractEventProducer<CommentLikeKafkaEvent> {

    public KafkaCommentLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public void sendEvent(CommentLikeKafkaEvent event) {
        super.sendEvent(event);
    }
}
