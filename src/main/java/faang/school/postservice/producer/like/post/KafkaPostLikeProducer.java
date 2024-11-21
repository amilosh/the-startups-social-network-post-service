package faang.school.postservice.producer.like.post;

import faang.school.postservice.config.properties.kafka.KafkaConfigurationProperties;
import faang.school.postservice.event.kafka.post.like.PostLikeKafkaEvent;
import faang.school.postservice.producer.like.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostLikeProducer implements KafkaMessageProducer<PostLikeKafkaEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    @Override
    public void sendMessage(PostLikeKafkaEvent message) {
        log.debug("Sending message to kafka topic of type {}", message.getEventType());
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
