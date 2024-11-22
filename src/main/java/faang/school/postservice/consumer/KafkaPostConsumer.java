package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostConsumer {
    @KafkaListener(topics ="${spring.data.kafka.topics.posts}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listen(PostDto postDto) {
        log.info("Post event received with author ID: {}", postDto.getAuthorId());
    }

}
