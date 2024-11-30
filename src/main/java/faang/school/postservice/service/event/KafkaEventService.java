package faang.school.postservice.service.event;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.PostPublishedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaEventService {
    private final UserServiceClient userServiceClient;
    private final PostPublishedProducer postPublishedProducer;

    public void sendEventToKafkaWhenPostPublished(Post post) {
        UserDto user = userServiceClient.getUser(post.getAuthorId());
        PostPublishedKafkaEvent event = new PostPublishedKafkaEvent(post.getId(), post.getAuthorId(), user.getFollowersIds());
        postPublishedProducer.sendEvent(event);
    }

    public void sendEventsToKafkaPostPublished(List<Post> posts) {
        List<Long> userIds = posts.stream()
                .map(Post::getAuthorId)
                .toList();
        List<UserDto> users = userServiceClient.getUsersByIds(userIds);
        Map<Long, List<Long>> userSubscribers = users.stream()
                .collect(Collectors.toMap(UserDto::getId, UserDto::getFollowersIds));
        posts.stream()
                .map(post -> new PostPublishedKafkaEvent(
                        post.getId(), post.getAuthorId(), userSubscribers.get(post.getAuthorId())
                ))
                .forEach(postPublishedProducer::sendEvent);
        log.info("{} posts published", posts.size());
    }
}
