package faang.school.postservice.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserExtendedFilterDto;
import faang.school.postservice.dto.user.UserResponseShortDto;
import faang.school.postservice.kafka.dto.PostKafkaDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class KafkaPostProducer {
    @Value("${kafka.topic.post-published-topic}")
    private String postPublishedTopic;

    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostsToKafka(List<Post> posts) {
        for (Post post : posts) {
            PostKafkaDto postKafkaDto = build(post);
            kafkaTemplate.send(postPublishedTopic, postKafkaDto);
        }
    }

    private PostKafkaDto build(Post post) {
        Long authorId = post.getAuthorId();
        List<Long> followerIds = userServiceClient.getFollowers(authorId, new UserExtendedFilterDto()).stream()
                .map(UserResponseShortDto::getId).toList();
        return PostKafkaDto.builder()
                .postId(post.getId())
                .followerIds(followerIds)
                .publishedAt(post.getPublishedAt())
                .build();
    }
}
