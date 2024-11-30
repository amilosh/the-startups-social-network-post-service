package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.message.PostPublishMessage;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KafkaPostProducer extends AbstractKafkaProducer<Post> {

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Value("${spring.kafka.topic.post-publisher}")
    private String topic;

    @Value("${spring.kafka.batch-size.post-publisher}")
    private int batchSize;


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
    protected List<Object> createMessages(Post post) {
        Long authorId = post.getAuthorId();
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        List<Long> followers = author.getFollowerIds();
        List<List<Long>> batches = ListUtils.partition(followers, batchSize);

        return batches.stream()
                .map(batch -> PostPublishMessage.builder()
                        .postId(post.getId())
                        .followerIds(batch)
                        .publishedAt(post.getPublishedAt())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
