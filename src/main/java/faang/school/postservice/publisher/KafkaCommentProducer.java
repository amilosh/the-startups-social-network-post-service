package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer implements EventPublisher<CommentEvent> {
    @Value("${spring.kafka.topics.comments.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final CommentEventMapper commentEventMapper;

    @Override
    public void publish(CommentEvent event) {
        kafkaTemplate.send(topic, commentEventMapper.toProto(event).toByteArray());
        log.info("event {} sent to topic {} ", event, topic);
    }
}
