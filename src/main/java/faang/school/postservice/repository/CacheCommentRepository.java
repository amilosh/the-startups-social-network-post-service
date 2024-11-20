package faang.school.postservice.repository;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.service.cache.SortedSetCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CacheCommentRepository implements CacheRepository<byte[]> {

    private final SortedSetCacheService<byte[]> sortedSetCacheService;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    public void save(String postId, byte[] byteComment) {
        Runnable runnable = () -> {
            String postIdKey = postId + "::comments_for_news_feed";
            sortedSetCacheService.put(postIdKey, byteComment, System.currentTimeMillis());

            if (sortedSetCacheService.size(postIdKey) >= newsFeedProperties.getLimitCommentsOnPost()) {
                sortedSetCacheService.popMin(postIdKey, byte[].class);
            }
        };

        sortedSetCacheService.runInOptimisticLock(runnable, postId);
    }
}
