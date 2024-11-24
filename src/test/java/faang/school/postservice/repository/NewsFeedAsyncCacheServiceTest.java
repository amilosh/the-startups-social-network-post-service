package faang.school.postservice.repository;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
import faang.school.postservice.service.cache.NewsFeedAsyncCacheService;
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
class NewsFeedAsyncCacheServiceTest {

    @Mock
    private SortedSetCacheRepository<Long> sortedSetCacheRepository;

    @Spy
    private NewsFeedProperties newsFeedProperties;

    @InjectMocks
    private NewsFeedAsyncCacheService newsFeedAsyncCacheService;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    private String userId;
    private Long postId;
    private Duration ttl;

    @BeforeEach
    void setUp() {
        int newsFeedSize = 3;
        int countHoursTimeToLive = 24;

        newsFeedProperties.setNewsFeedSize(newsFeedSize);
        newsFeedProperties.setCountHoursTimeToLive(countHoursTimeToLive);

        userId = "user123";
        postId = 42L;
        ttl = Duration.ofHours(countHoursTimeToLive);
    }

    @Test
    void save_shouldAddPostToCache() {
        when(sortedSetCacheRepository.size(userId)).thenReturn(4L);

        newsFeedAsyncCacheService.save(userId, postId);

        verify(sortedSetCacheRepository).executeInOptimisticLock(runnableArgumentCaptor.capture(), );
        runnableArgumentCaptor.getValue().run();
        verify(sortedSetCacheRepository).put(userId, postId, ttl);
        verify(sortedSetCacheRepository).popMin(userId, Long.class);
    }

    @Test
    void save_shouldRemoveOldestPostWhenSizeExceedsLimit() {
        when(sortedSetCacheRepository.size(userId)).thenReturn(0L);

        newsFeedAsyncCacheService.save(userId, postId);

        verify(sortedSetCacheRepository).executeInOptimisticLock(runnableArgumentCaptor.capture(), );
        runnableArgumentCaptor.getValue().run();
        verify(sortedSetCacheRepository).put(userId, postId, ttl);
        verify(sortedSetCacheRepository, never()).popMin(userId, Long.class);
    }
}
