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
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostProducer extends AbstractKafkaProducer<Post> {

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Value("${spring.kafka.topic.post-publisher}")
    private String topic;

    public KafkaPostProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                             UserServiceClient userServiceClient, UserContext userContext) {
        super(kafkaTemplate, objectMapper);
        this.userServiceClient = userServiceClient;
        this.userContext = userContext;
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected Object createMessage(Post post) {
        Long authorId = post.getAuthorId();
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        return PostPublishMessage.builder()
                .postId(post.getId())
                .followerIds(author.getFollowerIds())
                .build();
    }
}
