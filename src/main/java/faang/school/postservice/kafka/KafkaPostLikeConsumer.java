package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.PostLikeKafkaDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostLikeConsumer {
    private final PostService postService;

    @KafkaListener(
            topics = "${kafka.topic.post-liked-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostLikeKafkaDto postLikeKafkaDto, Acknowledgment acknowledgment) {
        try {
            postService.changeLike(postLikeKafkaDto.getPostId(), postLikeKafkaDto.getAction());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Like is not added to Post with id {}.", postLikeKafkaDto.getPostId());
            throw e;
        }
    }
}
