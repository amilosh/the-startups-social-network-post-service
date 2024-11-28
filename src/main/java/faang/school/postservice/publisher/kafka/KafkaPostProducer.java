package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostFeedEventDto;
import faang.school.postservice.exception.kafka.KafkaPublishPostException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.kafka.topics.posts-created}")
    private String topic;

    @Value("${post.pool-size}")
    private int poolSize;

    @Value("${post.publisher.batch-size}")
    private int batchSize;

    @Async
    public void sendPostEvent(Post post) {
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        List<Long> followerIds = getSubscriberIds(post.getAuthorId());
        List<List<Long>> partitionsFollowers = ListUtils.partition(followerIds, batchSize);

        for (List<Long> batchFollowers : partitionsFollowers) {
            executorService.execute(() -> {
                try {
                    publishMessage(post, batchFollowers);
                } catch (KafkaPublishPostException exception) {
                    log.error(exception.getMessage(), exception);
                }
            });
        }
    }

    @Retryable(retryFor = KafkaPublishPostException.class, backoff = @Backoff(
            delayExpression = "#{${post.publisher.retry.delay}}",
            multiplierExpression = "#{${post.publisher.retry.multiplier}}"))
    private void publishMessage(Post post, List<Long> followerIds) {
        PostFeedEventDto postDto = PostFeedEventDto.builder()
                .id(post.getId())
                .subscribersIds(followerIds)
                .publishedAt(post.getPublishedAt())
                .build();

        try {
            kafkaTemplate.send(topic, postDto);
        } catch (Exception exception) {
            throw new KafkaPublishPostException(
                    "Не отправлено событие для подписчиков [%s] пользователя %s".formatted(followerIds, post.getAuthorId()),
                    exception
            );
        }
    }

    @Retryable(retryFor = FeignException.class, backoff = @Backoff(
            delayExpression = "#{${post.publisher.retry.delay}}",
            multiplierExpression = "#{${post.publisher.retry.multiplier}}"))
    private List<Long> getSubscriberIds(long authorId) {
        List<Long> followerIds = userServiceClient.getFollowerIds(authorId);

        return followerIds;
    }

}
