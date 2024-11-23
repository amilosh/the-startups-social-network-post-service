package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PostViewKafkaConsumer implements KafkaConsumer<PostViewEventDto> {
    @Override
    @KafkaListener(
        id = "consumer-post-views",
        topics = "${spring.data.kafka.topic.post-view-topic.name}",
        containerFactory = "kafkaPostConsumerFactory"
    )
    public void listen(PostViewEventDto event) {

    }
}
