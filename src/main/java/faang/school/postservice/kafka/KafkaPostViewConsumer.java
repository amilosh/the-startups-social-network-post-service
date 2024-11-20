package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.PostViewKafkaDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostViewConsumer {
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.post-viewed-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostViewKafkaDto postViewKafkaDto, Acknowledgment acknowledgment) {
        try {
            postService.incrementView(postViewKafkaDto.getPostId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Post View with id {} is not added ???", postViewKafkaDto.getPostId());
            throw e;
        }
    }
}
