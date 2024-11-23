package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.kafka.event.PostEventDto;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final UserContext userContext;
    private final FeedMapper feedMapper;

    @Value("${feed.cache.max-feed-cache-size}")
    private final int maxFeedCacheSize;

    @Value("${feed.default-feed-amount}")
    private final int defaultFeedAmount;

    // TODO подумать что делать, если за время, пока сообщение шло по кафке пост успел удалиться
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttemptsExpression = "${feed.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${feed.retry.delay}")
    )
    @Transactional
    public void process(PostEventDto event) {
        long postId = event.postId();

        for (Long followerId : event.followerIds()) {
            Optional<FeedCache> feedOpt = redisFeedRepository.findById(followerId);
            FeedCache feedCache = feedOpt.orElseGet(() -> FeedCache.builder()
                .version(0L)
                .id(followerId)
                .postIds(new LinkedHashSet<>())
                .build());
            feedCache.getPostIds().add(postId);

            if (feedCache.getPostIds().size() > maxFeedCacheSize) {
                Iterator<Long> postIterator = feedCache.getPostIds().iterator();
                postIterator.next();
                postIterator.remove();
            }
            Feed feed = Feed.builder()
                .userId(followerId)
                .post(Post.builder().id(postId).build())
                .build();
            feedRepository.save(feed);
            redisFeedRepository.save(feedCache);
        }
    }

    public List<FeedDto> getFeed(long postId) {
        long userId = userContext.getUserId();
        Optional<FeedCache> feedCache = redisFeedRepository.findById(userId);
        List<FeedDto> feedDtos;
        if (feedCache.isEmpty()) {
            // тут подумать надо ли делать заполнение кэша из бд
            // такая ситуация может возникнуть, только если redis отключился и в момент прихода запроса кэш ещё
            // наполняется, как вариант не накатывать приложение пока не заполнится кэш
            List<Feed> feeds = getFeedFromDb(userId, postId, defaultFeedAmount);
            feedDtos = feedMapper.feedToFeedDto(feeds);
            log.info("Cache of feed for user: {} is empty, received {} feeds from db", userId, feeds.size());
            // пока кэш не наполняется из бд
        } else {
            LinkedHashSet<Long> postIds = feedCache.get().getPostIds();
            long differenceSize = defaultFeedAmount - postIds.size();
            feedDtos = new ArrayList<>(postIds.stream()
                .map(mapPostId -> FeedDto.builder().postId(mapPostId).build())
                .toList());
            int amountFromDb = 0;
            if (differenceSize > 0) {
                long maxPostId = Collections.max(postIds);
                List<Feed> feeds = getFeedFromDb(userId, maxPostId, differenceSize);
                List<FeedDto> feedDtosDFromDb = feedMapper.feedToFeedDto(feeds);
                feedDtos.addAll(feedDtosDFromDb);
                amountFromDb = feeds.size();
            }
            log.info("Received {} feeds for user {}: {} feeds from cache, {} feeds from db",
                feedDtos.size(), userId, postIds.size(), amountFromDb);
        }
        return feedDtos;
    }


    private List<Feed> getFeedFromDb(long userId, long postId, long feedAmount) {
        return feedRepository.findFeedsByUserId(userId, postId, feedAmount);
    }
}
