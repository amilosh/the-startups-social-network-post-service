package faang.school.postservice.publisher.kafka.publishers;

import faang.school.postservice.aop.aspects.publisher.Publisher;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.message.CommentPostMessage;
import faang.school.postservice.enums.publisher.PublisherType;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static faang.school.postservice.enums.publisher.PublisherType.COMMENT_POST;

@Getter
@RequiredArgsConstructor
@Component
public class CommentPostToKafkaPublisher implements Publisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final UserContext userContext;
    private final PublisherType type = COMMENT_POST;

    @Value("${spring.kafka.topic.post.comment}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }
        CommentPostMessage message = buildMessage((Comment) returnedValue);

        kafkaTemplate.send(topicName, message);
    }

    private CommentPostMessage buildMessage(Comment comment) {
        return CommentPostMessage.builder()
                .postId(comment.getPost().getId())
                .build();
    }
}
