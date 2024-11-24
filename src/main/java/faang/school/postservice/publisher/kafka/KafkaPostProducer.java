package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostFeedEventDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.kafka.topics.posts-channel}")
    private String topic;

    public void sendPostEvent(Post post) {
        PostFeedEventDto postDto = makeDto(post);

        kafkaTemplate.send(topic, postDto);
        log.info("Kafka sent event post: " + postDto);
    }

    private PostFeedEventDto makeDto(Post post) {
        List<Long> followerIds = userServiceClient.getFollowerIds(post.getAuthorId());

        return PostFeedEventDto.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .subscribersIds(followerIds)
                .build();
    }
}
