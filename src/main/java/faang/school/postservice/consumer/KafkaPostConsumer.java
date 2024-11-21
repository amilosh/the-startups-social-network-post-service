package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.post.PostCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaPostConsumer {

    @KafkaListener(topics = "${spring.data.kafka.topics.postsTopic.name}")
    public void listenPostEvent(PostCreateEvent event) {
        log.info("start listenPostEvent with: {}", event);


        System.out.println("Получено событие: " + event);
    }
}
