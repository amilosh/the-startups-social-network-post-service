package faang.school.postservice.aspect;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.PostPublishMessage;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventPublishKafkaAspect {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Value(value = "${spring.kafka.topic.post-publisher}")
    private String publishPostTopicName;

    @AfterReturning(pointcut = "@annotation(PostEventPublishKafka)", returning = "post")
    @Async("treadPool")
    public void publishPostAdvice(Post post) {
        Long authorId = post.getAuthorId();

        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        List<Long> followerId = author.getFollowerIds();

        PostPublishMessage postPublishMessage = new PostPublishMessage();
        postPublishMessage.setPostId(post.getId());
        postPublishMessage.setFollowersId(followerId);

        kafkaTemplate.send(publishPostTopicName, postPublishMessage);
        log.info("Message published to kafka broker: topic = {}, message = {}", publishPostTopicName, postPublishMessage);
    }
}
