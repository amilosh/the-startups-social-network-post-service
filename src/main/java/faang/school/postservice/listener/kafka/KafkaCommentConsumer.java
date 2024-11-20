package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.CommentEventKafka;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${spring.kafka.topics.comment}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaCommentConsumer {

    @KafkaHandler
    public void handleComment(CommentEventKafka event) {
        System.out.println("111111111"+event);
    }

}
