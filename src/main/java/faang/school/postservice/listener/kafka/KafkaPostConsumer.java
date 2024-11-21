package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.PostEventKafka;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${spring.kafka.topics.posts}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaPostConsumer {
    @KafkaHandler
    public void handlePost(PostEventKafka event) {
        System.out.println("22222222"+event);
    }
}
