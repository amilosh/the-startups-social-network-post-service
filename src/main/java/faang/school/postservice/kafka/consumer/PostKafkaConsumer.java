package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.kafka.PostCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PostKafkaConsumer extends AbstractKafkaConsumer<PostCreatedEvent> {

    @Override
    @KafkaListener(topics = "${kafka.topics.post}")
    public void consume(ConsumerRecord<String, PostCreatedEvent> record) {
        super.consume(record);
    }

    @Override
    protected void processEvent(PostCreatedEvent event) {
        // Custom logic to handle PostCreatedEvent
        System.out.println("Processing PostCreatedEvent: " + event);
    }
}
