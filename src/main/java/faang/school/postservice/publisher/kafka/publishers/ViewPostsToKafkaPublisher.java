package faang.school.postservice.publisher.kafka.publishers;

import faang.school.postservice.aop.aspects.publisher.Publisher;
import faang.school.postservice.dto.post.message.ViewPostMessage;
import faang.school.postservice.dto.post.serializable.PostViewEventParticipant;
import faang.school.postservice.enums.publisher.PublisherType;
import faang.school.postservice.publisher.kafka.publishers.util.builder.ViewPostMessageBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.enums.publisher.PublisherType.POST_VIEW;

@Getter
@RequiredArgsConstructor
@Component
public class ViewPostsToKafkaPublisher implements Publisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ViewPostMessageBuilder builder;
    private final PublisherType type = POST_VIEW;

    @Value("${spring.kafka.topic.post.view}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }

        ViewPostMessage message;

        if (returnedValue instanceof List) {
            message = builder.build((List<PostViewEventParticipant>) returnedValue);
        } else {
            message = builder.build(List.of((PostViewEventParticipant) returnedValue));
        }

        kafkaTemplate.send(topicName, message);
    }
}
