package faang.school.postservice.redis.service.impl;

import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import faang.school.postservice.redis.service.FeedCacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FeedCacheServiceImpl implements FeedCacheService {
    private final FeedsCacheRepository feedsCacheRepository;
    private final RedissonClient redissonClient;

    @Value("${feed.size}")
    private int feedSize;

    public FeedCacheServiceImpl(FeedsCacheRepository feedsCacheRepository, RedissonClient redissonClient) {
        this.feedsCacheRepository = feedsCacheRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    @Async("feedExecutor")
    public CompletableFuture<Void> getAndSaveFeed(Long feedId, Long postId) {
        log.debug("Lock acquired for feedId: {}", postId);
        String lockKey = "lock:" + feedId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            FeedCache feedCache = feedsCacheRepository.findById(feedId)
                    .orElseGet(() -> new FeedCache(feedId, new LinkedHashSet<>()));
            LinkedHashSet<Long> restoredPostIds = feedCache.getPostIds();

            Long lastPostId = findLastPostId(restoredPostIds);
            if (restoredPostIds.size() == feedSize) {
                restoredPostIds.remove(lastPostId);
            }

            LinkedHashSet<Long> newPostIds = new LinkedHashSet<>(Collections.singleton(postId));
            newPostIds.addAll(restoredPostIds);
            feedCache.setPostIds(newPostIds);

            feedsCacheRepository.save(feedCache);
            log.info("Successfully added postId to feed : {}", postId);
            return CompletableFuture.completedFuture(null);
        } finally {
            lock.unlock();
            log.debug("Lock released for feedId: {}", feedId);
        }
    }

    private Long findLastPostId(LinkedHashSet<Long> postIds) {
        Long lastPostId = null;
        for (Long id : postIds) {
            lastPostId = id;
        }
        return lastPostId;
    }
}
