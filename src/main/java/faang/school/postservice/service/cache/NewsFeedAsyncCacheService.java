package faang.school.postservice.service.cache;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NewsFeedAsyncCacheService implements AsyncCacheService<Long> {

    private final SortedSetCacheRepository<Long> sortedSetCacheRepository;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    @Async("newsFeedThreadPoolExecutor")
    public CompletableFuture<Long> save(String followerId, Long postId) {
        String followerFeedKey = followerId + "::news_feed";
        Runnable runnable = () -> {
            sortedSetCacheRepository.put(followerFeedKey, postId, System.currentTimeMillis());

            if (sortedSetCacheRepository.size(followerFeedKey) >= newsFeedProperties.getNewsFeedSize()) {
                sortedSetCacheRepository.popMin(followerFeedKey);
            }
        };

        sortedSetCacheRepository.executeInOptimisticLock(runnable, followerFeedKey);
        return CompletableFuture.completedFuture(postId);
    }
}
