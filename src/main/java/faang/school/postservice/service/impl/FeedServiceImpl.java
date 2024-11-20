package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.model.Feed;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedCacheRepository cacheRepository;
    private final RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate;

    @Value("${feed.cache.count}")
    private int cacheCount;

    @Override
    public void distributePostsToUsersFeeds(PostPublishedEvent event) {
        List<Long> subsIds = event.getSubscribersIds();

        subsIds.forEach(id -> updateFeed(id, event.getPostId()));
    }

    @Retryable(maxAttempts = 5, retryFor = {OptimisticEntityLockException.class})
    private void updateFeed(long userId, long postId) {
        redisTemplate.watch(userId);
        Feed value = cacheRepository.findByUserId(userId).orElse(new Feed(userId, new LinkedHashSet<>()));
        LinkedHashSet<Long> hashSet = value.getPostIds();

        redisTemplate.multi();
        hashSet.add(postId);
        if (hashSet.size() > cacheCount) {
            hashSet.remove(hashSet.iterator().next());
        }
        value.setPostIds(hashSet);

        if (redisTemplate.exec().isEmpty()) {
            throw new OptimisticEntityLockException(value, "parallel modifying");
        }

        redisTemplate.unwatch();
    }
}
