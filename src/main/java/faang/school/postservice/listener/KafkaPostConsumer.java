package faang.school.postservice.listener;

import faang.school.postservice.model.event.PostEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostConsumer {
    @KafkaListener(topics = "post_channel", groupId = "post_event")
    public void onPostEvent(PostEvent postEvent) {
        System.out.println("Kafka post event received: " + postEvent);
    }
}
