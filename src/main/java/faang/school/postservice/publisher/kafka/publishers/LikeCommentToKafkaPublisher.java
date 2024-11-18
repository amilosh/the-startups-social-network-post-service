package faang.school.postservice.publisher.kafka.publishers;

import faang.school.postservice.aop.aspects.publisher.Publisher;
import faang.school.postservice.dto.post.message.LikeCommentMessage;
import faang.school.postservice.enums.publisher.PublisherType;
import faang.school.postservice.model.Like;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static faang.school.postservice.enums.publisher.PublisherType.LIKE_POST_COMMENT;

@Getter
@RequiredArgsConstructor
@Component
public class LikeCommentToKafkaPublisher implements Publisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PublisherType type = LIKE_POST_COMMENT;

    @Value("${spring.kafka.topic.post.like_post_comment}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }
        LikeCommentMessage message = buildMessage((Like) returnedValue);

        kafkaTemplate.send(topicName, message);
    }

    private LikeCommentMessage buildMessage(Like like) {
        return LikeCommentMessage.builder()
                .postId(like.getComment().getPost().getId())
                .commentId(like.getComment().getId())
                .build();
    }
}
