package faang.school.postservice.app.listener;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.kafka.producer.PostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.mapper.UserWithFollowersMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.UserWithFollowersDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.model.event.application.PostsPublishCommittedEvent;
import faang.school.postservice.model.event.kafka.PostPublishedEvent;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostPublishCommitedEventListener {

    private static final int REFRESH_TIME_IN_HOURS = 3;

    @Value("${kafka.batch-size.follower:1000}")
    private int followerBatchSize;
    @Value("${system-user-id}")
    private int systemUserId;

    private final RedisPostService redisPostService;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final PostProducer postProducer;
    private final UserShortInfoRepository userShortInfoRepository;
    private final RedisUserService redisUserService;
    private final UserWithFollowersMapper userWithFollowersMapper;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final UserContext userContext;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostsPublishCommittedEvent(PostsPublishCommittedEvent event) {
        List<Post> posts = event.getPosts();
        log.info("Processing committed posts: {}", posts.size());

        posts.forEach(post -> {
            log.debug("Saving author of post (user with id = {}) in DB and Redis", post.getAuthorId());
            updateUserShortInfoIfStale(post.getAuthorId());

            log.debug("Saving post with id = {} in Redis if needed", post.getId());
            PostDto postDto = postMapper.toPostDto(post);
            RedisPostDto redisPostDto = redisPostDtoMapper.mapToRedisPostDto(postDto);
            //TODO может нужен просто save
            redisPostService.savePostIfNotExists(redisPostDto);

            log.debug("Start sending PostPublishedEvent for post with id = {} to Kafka", post.getId());
            List<Long> followerIds = redisUserService.getFollowerIds(post.getAuthorId());

            if (followerIds.isEmpty()) {
                return;
            }

            for (int indexFrom = 0; indexFrom < followerIds.size(); indexFrom += followerBatchSize) {
                int indexTo = Math.min(indexFrom + followerBatchSize, followerIds.size());
                PostPublishedEvent subEvent = new PostPublishedEvent(
                        postDto.getId(),
                        followerIds.subList(indexFrom, indexTo),
                        postDto.getPublishedAt());
                postProducer.sendEvent(subEvent);
            }
        });
    }

    private void updateUserShortInfoIfStale(Long userId) {
        userContext.setUserId(systemUserId);
        Optional<LocalDateTime> lastSavedAt = userShortInfoRepository.findLastSavedAtByUserId(userId);
        if (lastSavedAt.isEmpty() || lastSavedAt.get().isBefore(LocalDateTime.now().minusHours(REFRESH_TIME_IN_HOURS))) {
            UserWithFollowersDto userWithFollowers = userServiceClient.getUserWithFollowers(userId);
            UserShortInfo userShortInfo = userWithFollowersMapper.toUserShortInfo(userWithFollowers);
            userShortInfoRepository.save(userShortInfo);
            RedisUserDto redisUserDto = userWithFollowersMapper.toRedisUserDto(userWithFollowers);
            redisUserDto.setUpdatedAt(LocalDateTime.now());
            redisUserService.saveUser(redisUserDto);
        }
    }
}
