package faang.school.postservice.repository;

import faang.school.postservice.config.CachePostProperties;
import faang.school.postservice.service.cache.SortedSetCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class AsyncCacheFeedRepository implements AsyncCacheRepository<Long> {

    private final SortedSetCacheService<Long> sortedSetCacheService;
    private final CachePostProperties cachePostProperties;

    @Override
    @Async("newsFeedThreadPoolExecutor")
    public CompletableFuture<Long> save(String followerId, Long postId) {
        Runnable runnable = () -> {
            String followerFeedKey = followerId + "::list";
            sortedSetCacheService.put(followerFeedKey, postId, System.currentTimeMillis());

            if (sortedSetCacheService.size(followerFeedKey) > cachePostProperties.getNewsFeedSize()) {
                sortedSetCacheService.leftPop(followerFeedKey, Long.class);
            }
        };

        sortedSetCacheService.runInOptimisticLock(runnable, followerId);
        return CompletableFuture.completedFuture(postId);
    }
}
