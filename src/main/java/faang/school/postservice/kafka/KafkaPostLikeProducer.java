package faang.school.postservice.kafka;

import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.dto.PostLikeKafkaDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaPostLikeProducer {
    @Value("${kafka.topic.post-liked-topic}")
    private String postLikedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostLikeToKafka(Like like, LikeAction action) {
        PostLikeKafkaDto postLikeKafkaDto = build(like, action);
        kafkaTemplate.send(postLikedTopic, postLikeKafkaDto);
    }

    public void sendPostLikeToKafka(Post post, LikeAction action) {
        PostLikeKafkaDto postLikeKafkaDto = build(post, action);
        kafkaTemplate.send(postLikedTopic, postLikeKafkaDto);
    }

    private PostLikeKafkaDto build(Like like, LikeAction action) {
        return PostLikeKafkaDto.builder()
                .postId(like.getPost().getId())
                .action(action)
                .build();
    }

    private PostLikeKafkaDto build(Post post, LikeAction action) {
        return PostLikeKafkaDto.builder()
                .postId(post.getId())
                .action(action)
                .build();
    }
}
