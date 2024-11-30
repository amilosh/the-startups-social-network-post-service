package faang.school.postservice.kafka.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserExtendedFilterDto;
import faang.school.postservice.dto.user.UserResponseShortDto;
import faang.school.postservice.kafka.post.event.PostPublishedKafkaEvent;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.utils.AppCollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;

@RequiredArgsConstructor
@Component
public class PostKafkaProducer {
    @Value("${kafka.topic.post-published-topic.name}")
    private String postPublishedTopic;

    @Value("${kafka.topic.post-published-topic.batch-size}")
    private int postPublishedTopicBatchSize;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final UserServiceClient userServiceClient;

    public void sendPostsToKafka(List<Post> posts) {
        for (Post post : posts) {
            List<PostPublishedKafkaEvent> postPublishedKafkaEvents = mapToPostPublishedKafkaEvent(post);
            postPublishedKafkaEvents.forEach(event -> {
                kafkaTemplate.send(postPublishedTopic, event);
            });
        }
    }

    private List<PostPublishedKafkaEvent> mapToPostPublishedKafkaEvent(Post post) {
        List<Long> followerIds = userServiceClient.getFollowers(post.getAuthorId(), new UserExtendedFilterDto()).stream()
                .map(UserResponseShortDto::getId)
                .toList();

        if (followerIds.size() <= postPublishedTopicBatchSize) {
            return singletonList(new PostPublishedKafkaEvent(post.getId(), followerIds, post.getPublishedAt()));
        } else {
            List<List<Long>> subLists = AppCollectionUtils.getListOfBatches(followerIds, postPublishedTopicBatchSize);
            return subLists.stream()
                    .map(batch -> new PostPublishedKafkaEvent(post.getId(), batch, post.getPublishedAt()))
                    .toList();
        }
    }
}
