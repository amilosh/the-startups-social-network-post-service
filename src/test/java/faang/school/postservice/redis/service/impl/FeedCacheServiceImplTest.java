package faang.school.postservice.redis.service.impl;

import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedCacheServiceImplTest {
// TODO asdfas

    @Mock
    RedissonClient redissonClient;

    @Mock
    FeedsCacheRepository feedsCacheRepository;

    @Mock
    RLock lock;

    @InjectMocks
    FeedCacheServiceImpl feedCacheService;

    @Captor
    ArgumentCaptor<FeedCache> feedCacheArgumentCaptor = ArgumentCaptor.forClass(FeedCache.class);

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);


    @Test
    public void testGetAndSaveFeedSuccess() {
        Long feedId = 1L;
        Long postId = 3L;
        FeedCache feedCache = FeedCache.builder()
                .postIds(new LinkedHashSet<>(Arrays.asList(1L, 2L)))
                .id(feedId)
                .build();

        when(redissonClient.getLock("lock:"+feedId)).thenReturn(lock);
        when(feedsCacheRepository.findById(feedId)).thenReturn(Optional.ofNullable(feedCache));

        feedCacheService.getAndSaveFeed(feedId, postId);

        verify(feedsCacheRepository, times(1)).save(feedCacheArgumentCaptor.capture());
        verify(redissonClient, times(1)).getLock(stringArgumentCaptor.capture());

        FeedCache resultFeedCache = feedCacheArgumentCaptor.getValue();
        Assertions.assertAll(
                () -> assertEquals(feedId, resultFeedCache.getId()),
                () -> assertEquals(Set.of(3L, 1L, 2L), resultFeedCache.getPostIds()),
                () -> assertEquals("lock:" + feedId, stringArgumentCaptor.getValue())
        );

    }
}