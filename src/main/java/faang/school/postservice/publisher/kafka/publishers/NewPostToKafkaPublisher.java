package faang.school.postservice.publisher.kafka.publishers;

import faang.school.postservice.aop.aspects.publisher.Publisher;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.enums.publisher.PublisherType;
import faang.school.postservice.model.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

import static faang.school.postservice.enums.publisher.PublisherType.NEW_POST;

@Getter
@RequiredArgsConstructor
@Component
public class NewPostToKafkaPublisher implements Publisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final PublisherType type = NEW_POST;

    @Value("${spring.kafka.topic.post.new}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }
        NewPostMessage message = buildMessage((Post) returnedValue);

        kafkaTemplate.send(topicName, message);
    }

    private NewPostMessage buildMessage(Post post) {
        return NewPostMessage.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .createdAtTimestamp(post.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                .followersIds(getFollowersId(post.getAuthorId()))
                .build();
    }

    private List<Long> getFollowersId(Long authorId) {
        if (authorId == null) {
            return List.of();
        }
        return userServiceClient.getFollowersId(authorId);
    }
}
