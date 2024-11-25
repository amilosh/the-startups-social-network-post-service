package faang.school.postservice.redis.service.impl;

import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import faang.school.postservice.redis.service.FeedCacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Service
public class FeedCacheServiceImpl implements FeedCacheService {
    private final FeedsCacheRepository feedsCacheRepository;

    @Value("${feed.size}")
    private int feedSize;

    public FeedCacheServiceImpl(FeedsCacheRepository feedsCacheRepository) {
        this.feedsCacheRepository = feedsCacheRepository;
    }

    @Override
    @Async("feedExecutor")
    public CompletableFuture<Void> getAndSaveFeed(Long feedId, Long postId) {
        FeedCache feedCache = feedsCacheRepository.findById(feedId)
                .orElseGet(() -> new FeedCache(feedId,
                        new TreeSet<>()));

        TreeSet<Long> restoredPostIds = new TreeSet<>(Comparator.reverseOrder());
        restoredPostIds.addAll(feedCache.getPostIds());
        if (restoredPostIds.size() == feedSize) {
            restoredPostIds.pollLast();
        }
        restoredPostIds.add(postId);
        feedCache.setPostIds(restoredPostIds);

        feedsCacheRepository.save(feedCache);

        return CompletableFuture.completedFuture(null);
    }
}
