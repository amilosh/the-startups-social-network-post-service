package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.event.CommentEventDto;
import faang.school.postservice.dto.kafka.event.LikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.producer.KafkaLikeProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeEventService {
    private final KafkaLikeProducer producer;
    private final KafkaSerializer kafkaSerializer;

    @Async("kafkaProducerConsumerExecutor")
    public void produce(LikeDto likeDto) {
        LikeEventDto eventDto = LikeEventDto.builder()
            .postId(likeDto.postId())
            .authorId(likeDto.userId())
            .build();
        String event = kafkaSerializer.serialize(eventDto);
        producer.send(event);
    }
}
