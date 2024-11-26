package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.PostPublishMessage;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class KafkaPostProducer extends KafkaEventProducer {
    private UserServiceClient userServiceClient;
    private UserContext userContext;

    public KafkaPostProducer(KafkaTemplate<String, String> kafkaTemplate,
                             @Value("${spring.kafka.topic.post-publisher}") String topicName,
                             ObjectMapper mapper) {
        super(kafkaTemplate, topicName, mapper);
    }

    @Async("treadPool")
    public void publishPost(Post post) {
        Long authorId = post.getAuthorId();
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        List<Long> followerIds = author.getFollowerIds();

        PostPublishMessage postPublishMessage = PostPublishMessage.builder()
                .postId(post.getId())
                .followerIds(followerIds)
                .build();

        publishEvent(postPublishMessage);

    }
}
