package faang.school.postservice.listener.kafka;

import org.springframework.kafka.annotation.KafkaListener;

@KafkaListener(topics = "${spring.kafka.topics.comment}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaCommentConsumer {

}
