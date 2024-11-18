package faang.school.postservice.publisher.kafka.publishers;

import faang.school.postservice.aop.aspects.publisher.Publisher;
import faang.school.postservice.dto.post.message.ViewPostMessage;
import faang.school.postservice.dto.post.serializable.PostViewEventParticipant;
import faang.school.postservice.enums.publisher.PublisherType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static faang.school.postservice.enums.publisher.PublisherType.VIEW_POST;

@Getter
@RequiredArgsConstructor
@Component
public class ViewPostsToKafkaPublisher implements Publisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PublisherType type = VIEW_POST;

    @Value("${spring.kafka.topic.post.view}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }
        ViewPostMessage message = buildMessage(returnedValue);

        kafkaTemplate.send(topicName, message);
    }

    private ViewPostMessage buildMessage(Object returnedValue) {
        List<PostViewEventParticipant> posts;

        if (returnedValue instanceof List) {
            posts = (List<PostViewEventParticipant>) returnedValue;
        } else {
            posts = List.of((PostViewEventParticipant) returnedValue);
        }

        List<Long> postIds = posts.stream()
                .filter(Objects::nonNull)
                .map(PostViewEventParticipant::getId)
                .toList();

        return ViewPostMessage.builder()
                .postsIds(postIds)
                .build();
    }
}
