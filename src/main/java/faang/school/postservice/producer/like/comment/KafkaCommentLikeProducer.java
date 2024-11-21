package faang.school.postservice.producer.like.comment;

import faang.school.postservice.config.properties.kafka.KafkaConfigurationProperties;
import faang.school.postservice.event.kafka.comment.like.CommentLikeKafkaEvent;
import faang.school.postservice.producer.like.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentLikeProducer implements KafkaMessageProducer<CommentLikeKafkaEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    @Override
    public void sendMessage(CommentLikeKafkaEvent message) {
        kafkaTemplate.send(kafkaConfigurationProperties.getTopic().getLikeTopic(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message! ", ex);
                    } else {
                        if (result != null) {
                            log.debug("Message sent successfully to topic {} with offset {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().offset());
                        }
                    }
                });
    }
}
