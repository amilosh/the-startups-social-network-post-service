package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.event.LikeEventDto;
import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.producer.KafkaPostViewProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewEventService {
    private final KafkaPostViewProducer producer;
    private final KafkaSerializer kafkaSerializer;

    @Async("kafkaProducerConsumerExecutor")
    public void produce(Long userId, PostDto postDto) {
        PostViewEventDto eventDto = PostViewEventDto.builder()
            .postId(postDto.id())
            .userId(userId)
            .build();
        String event = kafkaSerializer.serialize(eventDto);
        producer.send(event);
    }
}
