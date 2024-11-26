package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.PostPublishMessage;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class KafkaPostProducer extends KafkaEventProducer {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public KafkaPostProducer(KafkaTemplate<String, String> kafkaTemplate,
                             @Value("${spring.kafka.topic.post-publisher}") String topicName,
                             ObjectMapper mapper,
                             UserServiceClient userServiceClient,
                             UserContext userContext) {
        super(kafkaTemplate, topicName, mapper);
        this.userContext = userContext;
        this.userServiceClient = userServiceClient;
    }

    @Async("treadPool")
    public void publishPost(Post post) {
        Long authorId = post.getAuthorId();
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        List<Long> followerIds = author.getFollowerIds();

        if (!followerIds.isEmpty()) {
            PostPublishMessage postPublishMessage = PostPublishMessage.builder()
                    .postId(post.getId())
                    .followerIds(followerIds)
                    .build();

            publishEvent(postPublishMessage);
        } else {
            log.info("Post not sent to kafka, this author{} has no subscribers", authorId);
        }
    }
}
