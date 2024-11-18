package faang.school.postservice.repository;

import faang.school.postservice.config.CachePostProperties;
import faang.school.postservice.service.cache.ListCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
@RequiredArgsConstructor
public class AsyncCacheFeedRepository implements AsyncCacheRepository<Long> {

    private final ListCacheService<Long> listCacheService;
    private final CachePostProperties cachePostProperties;

    @Override
    @Async("newsFeedThreadPoolExecutor")
    public CompletableFuture<Long> save(String followerId, Long postId) {
        Runnable runnable = () -> {
            listCacheService.put(followerId, postId);

            if (listCacheService.size(followerId) > cachePostProperties.getNewsFeedSize()) {
                listCacheService.leftPop(followerId, Long.class);
            }
        };

        listCacheService.runInOptimisticLock(runnable);
        return CompletableFuture.completedFuture(postId);
    }
}
