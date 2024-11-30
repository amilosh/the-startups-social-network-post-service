package faang.school.postservice.publisher.kafka.publishers;

import faang.school.postservice.aop.aspects.publisher.Publisher;
import faang.school.postservice.dto.post.message.LikeForCommentMessage;
import faang.school.postservice.enums.publisher.PublisherType;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.kafka.publishers.util.builder.LikeForCommentMessageBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static faang.school.postservice.enums.publisher.PublisherType.COMMENT_LIKE;

@Getter
@RequiredArgsConstructor
@Component
public class LikeCommentToKafkaPublisher implements Publisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final LikeForCommentMessageBuilder builder;
    private final PublisherType type = COMMENT_LIKE;

    @Value("${spring.kafka.topic.post.like_post_comment}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }
        LikeForCommentMessage message = builder.build((Like) returnedValue);

        kafkaTemplate.send(topicName, message);
    }
}
