package faang.school.postservice.kafka;

import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.kafka.dto.PostViewKafkaDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class KafkaPostViewProducer {
    @Value("${kafka.topic.post-viewed-topic}")
    private String postViewedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostViewToKafka(Post post) {
        PostViewKafkaDto postViewKafkaDto = build(post);
        kafkaTemplate.send(postViewedTopic, postViewKafkaDto);
    }

    public void sendPostViewsToKafka(List<Post> posts) {
        posts.forEach(this::sendPostViewToKafka);
    }

    public void sendPostViewToKafka(PostFeedResponseDto post) {
        PostViewKafkaDto postViewKafkaDto = build(post);
        kafkaTemplate.send(postViewedTopic, postViewKafkaDto);
    }

    public void sendPostViewsDtoToKafka(List<PostFeedResponseDto> posts) {
        posts.forEach(this::sendPostViewToKafka);
    }

    private PostViewKafkaDto build(Post post) {
        return PostViewKafkaDto.builder()
                .postId(post.getId())
                .build();
    }

    private PostViewKafkaDto build(PostFeedResponseDto post) {
        return PostViewKafkaDto.builder()
                .postId(post.getId())
                .build();
    }
}
