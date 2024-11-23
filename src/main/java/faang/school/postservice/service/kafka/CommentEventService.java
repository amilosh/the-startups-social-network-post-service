package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.kafka.event.CommentEventDto;
import faang.school.postservice.producer.KafkaCommentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventService {
    private final KafkaCommentProducer producer;
    private final KafkaSerializer kafkaSerializer;

    @Async("kafkaProducerConsumerExecutor")
    public void produce(Long postId, CommentDto commentDto) {
        CommentEventDto eventDto = CommentEventDto.builder()
            .postId(postId)
            .commentId(commentDto.id())
            .authorId(commentDto.authorId())
            .build();
        String event = kafkaSerializer.serialize(eventDto);
        producer.send(event);
    }
}
