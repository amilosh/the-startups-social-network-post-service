package faang.school.postservice.repository;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.service.cache.SortedSetCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class AsyncCacheFeedRepository implements AsyncCacheRepository<Long> {

    private final SortedSetCacheService<Long> sortedSetCacheService;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    @Async("newsFeedThreadPoolExecutor")
    public CompletableFuture<Long> save(String followerId, Long postId) {
        Runnable runnable = () -> {
            String followerFeedKey = followerId + "::news_feed";
            sortedSetCacheService.put(followerFeedKey, postId, System.currentTimeMillis());

            if (sortedSetCacheService.size(followerFeedKey) >= newsFeedProperties.getNewsFeedSize()) {
                sortedSetCacheService.popMin(followerFeedKey, Long.class);
            }
        };

        sortedSetCacheService.runInOptimisticLock(runnable, followerId);
        return CompletableFuture.completedFuture(postId);
    }
}
