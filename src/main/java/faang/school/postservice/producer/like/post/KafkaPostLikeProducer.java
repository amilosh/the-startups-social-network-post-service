package faang.school.postservice.producer.like.post;

import faang.school.postservice.event.kafka.post.like.PostLikeKafkaEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaPostLikeProducer extends AbstractEventProducer<PostLikeKafkaEvent> {

    public KafkaPostLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public void sendEvent(PostLikeKafkaEvent event) {
        super.sendEvent(event);
    }
}
