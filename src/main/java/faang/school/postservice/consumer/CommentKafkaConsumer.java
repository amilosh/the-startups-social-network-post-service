package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.CommentEventDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CommentKafkaConsumer implements KafkaConsumer<CommentEventDto> {
    @Override
    @KafkaListener(
        id = "consumer-comments",
        topics = "${spring.data.kafka.topic.comment-topic.name}",
        containerFactory = "kafkaCommentConsumerFactory"
    )
    public void listen(CommentEventDto event) {

    }
}
