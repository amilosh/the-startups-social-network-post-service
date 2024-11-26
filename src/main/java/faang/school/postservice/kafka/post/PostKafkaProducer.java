package faang.school.postservice.kafka.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.dto.user.UserExtendedFilterDto;
import faang.school.postservice.dto.user.UserResponseShortDto;
import faang.school.postservice.kafka.post.event.PostPublishedKafkaEvent;
import faang.school.postservice.kafka.post.event.PostViewedKafkaEvent;
import faang.school.postservice.model.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PostKafkaProducer {
    @Value("${kafka.topic.post-published-topic}")
    private String postPublishedTopic;

    @Value("${kafka.topic.post-viewed-topic}")
    private String postViewedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final UserServiceClient userServiceClient;

    public void sendPostsToKafka(List<Post> posts) {
        for (Post post : posts) {
            PostPublishedKafkaEvent postPublishedKafkaEvent = mapToPostPublishedKafkaEvent(post);
            kafkaTemplate.send(postPublishedTopic, postPublishedKafkaEvent);
        }
    }

    public void sendPostViewToKafka(Post post) {
        PostViewedKafkaEvent postViewedKafkaEvent = mapToPostViewKafkaDto(post);
        kafkaTemplate.send(postViewedTopic, postViewedKafkaEvent);
    }

    public void sendPostViewsToKafka(List<Post> posts) {
        posts.forEach(this::sendPostViewToKafka);
    }

    public void sendPostViewToKafka(PostFeedResponseDto post) {
        PostViewedKafkaEvent postViewedKafkaEvent = build(post);
        kafkaTemplate.send(postViewedTopic, postViewedKafkaEvent);
    }

    public void sendPostViewsDtoToKafka(List<PostFeedResponseDto> posts) {
        posts.forEach(this::sendPostViewToKafka);
    }

    private PostPublishedKafkaEvent mapToPostPublishedKafkaEvent(Post post) {
        List<Long> followerIds = userServiceClient.getFollowers(post.getAuthorId(), new UserExtendedFilterDto()).stream()
                .map(UserResponseShortDto::getId)
                .toList();
        return new PostPublishedKafkaEvent(post.getId(), followerIds, post.getPublishedAt());
    }

    private PostViewedKafkaEvent mapToPostViewKafkaDto(Post post) {
        return PostViewedKafkaEvent.builder().postId(post.getId()).build();
    }

    private PostViewedKafkaEvent build(PostFeedResponseDto post) {
        return PostViewedKafkaEvent.builder().postId(post.getId()).build();
    }
}
