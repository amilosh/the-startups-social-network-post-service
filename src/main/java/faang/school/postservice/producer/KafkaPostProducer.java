package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.PostPublishMessage;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer implements KafkaMessageProducer<Post> {
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;
    private final UserContext userContext;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.post-publisher}")
    private String topic;

    @Override
    @Async("treadPool")
    public void publish(Post post) {
        Long authorId = post.getAuthorId();
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        List<Long> followerIds = author.getFollowerIds();

        if (!followerIds.isEmpty()) {
            try {
                PostPublishMessage postMessage = PostPublishMessage.builder()
                        .postId(post.getId())
                        .followerIds(followerIds)
                        .build();

                String message = objectMapper.writeValueAsString(postMessage);
                kafkaTemplate.send(topic, message);
                log.info("Sent message to kafka Topic: {} Message: {}", topic, message);
            } catch (JsonProcessingException e) {
                log.error("Failed to convert object to json");
            }
        } else {
            log.info("Post not sent to kafka, this author {} has no subscribers", authorId);
        }
    }
}
