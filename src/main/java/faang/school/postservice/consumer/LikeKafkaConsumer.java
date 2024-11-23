package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.LikeEventDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LikeKafkaConsumer implements KafkaConsumer<LikeEventDto> {
    @Override
    @KafkaListener(
        id = "consumer-likes",
        topics = "${spring.data.kafka.topic.like-topic.name}",
        containerFactory = "kafkaLikeConsumerFactory"
    )
    public void listen(LikeEventDto event) {

    }
}
