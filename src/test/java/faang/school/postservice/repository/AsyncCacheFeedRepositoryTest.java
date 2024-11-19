package faang.school.postservice.repository;

import faang.school.postservice.config.CachePostProperties;
import faang.school.postservice.service.cache.SortedSetCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncCacheFeedRepositoryTest {

    @Mock
    private SortedSetCacheService<Long> sortedSetCacheService;

    @Spy
    private CachePostProperties cachePostProperties;

    @InjectMocks
    private AsyncCacheFeedRepository asyncCacheFeedRepository;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    private String userId;
    private Long postId;
    private Duration ttl;

    @BeforeEach
    void setUp() {
        int newsFeedSize = 3;
        int countHoursTimeToLive = 24;

        cachePostProperties.setNewsFeedSize(newsFeedSize);
        cachePostProperties.setCountHoursTimeToLive(countHoursTimeToLive);

        userId = "user123";
        postId = 42L;
        ttl = Duration.ofHours(countHoursTimeToLive);
    }

    @Test
    void save_shouldAddPostToCache() {
        when(sortedSetCacheService.size(userId)).thenReturn(4L);

        asyncCacheFeedRepository.save(userId, postId);

        verify(sortedSetCacheService).runInOptimisticLock(runnableArgumentCaptor.capture(), );
        runnableArgumentCaptor.getValue().run();
        verify(sortedSetCacheService).put(userId, postId, ttl);
        verify(sortedSetCacheService).leftPop(userId, Long.class);
    }

    @Test
    void save_shouldRemoveOldestPostWhenSizeExceedsLimit() {
        when(sortedSetCacheService.size(userId)).thenReturn(0L);

        asyncCacheFeedRepository.save(userId, postId);

        verify(sortedSetCacheService).runInOptimisticLock(runnableArgumentCaptor.capture(), );
        runnableArgumentCaptor.getValue().run();
        verify(sortedSetCacheService).put(userId, postId, ttl);
        verify(sortedSetCacheService, never()).leftPop(userId, Long.class);
    }
}
