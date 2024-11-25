package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFeedEventDto;
import faang.school.postservice.dto.user.UserAmountDto;
import faang.school.postservice.dto.user.UserExtendedFilterDto;
import faang.school.postservice.dto.user.UserResponseShortDto;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserContext userContext;

    @Value("${spring.data.kafka.topics.posts-created}")
    private String topic;

    @Value("${post.pool-size}")
    private int poolSize;

    @Value("${post.publisher.batch-size}")
    private int batchSize;

    @Async("singleThreadExecutor")
    public void sendPostEvent(Post post) {
        setTechnicalUserInContext();

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        UserAmountDto followersCount = userServiceClient.getFollowersCount(post.getAuthorId());
        int pageCount = (followersCount.getUserAmount() + batchSize - 1) / batchSize;

        for (int page = 0; page < pageCount; page++) {
            int finalPage = page;
            executorService.execute(() -> {
                setTechnicalUserInContext();
                publishMessage(post, finalPage, batchSize);
            });
        }
    }

    private void publishMessage(Post post, int page, int pageSize) {
        List<Long> followerIds = getSubscribersByPages(post.getAuthorId(), page, pageSize);
        PostFeedEventDto postDto = PostFeedEventDto.builder()
                .id(post.getId())
                .subscribersIds(followerIds)
                .publishedAt(post.getPublishedAt())
                .build();

        try {
            kafkaTemplate.send(topic, postDto);
        } catch (Exception exception) {
            log.error("Не была обработана page={} pageSize={} часть подписчиков пользователя {}",
                    page, pageSize, post.getAuthorId(), exception);
        }
    }

    @Retryable(retryFor = FeignException.class, backoff = @Backoff(
            delayExpression = "#{${post.publisher.retry.delay}}",
            multiplierExpression = "#{${post.publisher.retry.multiplier}}"))
    private List<Long> getSubscribersByPages(long authorId, int page, int pageSize) {
        UserExtendedFilterDto filter = UserExtendedFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .build();
        List<UserResponseShortDto> followers = userServiceClient.getFollowers(authorId, filter);

        return followers.stream()
                .map(UserResponseShortDto::getId)
                .toList();
    }

    private void setTechnicalUserInContext() {
        userContext.setUserId(-1);
    }
}
