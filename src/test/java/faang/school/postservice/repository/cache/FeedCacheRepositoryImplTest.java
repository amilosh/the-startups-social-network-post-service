package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.cache.feed.FeedCacheDto;
import faang.school.postservice.repository.cache.feed.FeedCacheRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FeedCacheRepositoryImplTest {

    @InjectMocks
    private FeedCacheRepositoryImpl feedCacheRepository;

    @Mock
    private RedisProperties redisProperties;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private Comparator<Long> comparator = Comparator.reverseOrder();

    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    private static final Long ID_THREE = 3L;
    private static final Long ID_FOUR = 4L;
    private ValueOperations<String, Object> valueOperations;
    private FeedCacheDto feedCacheDto;
    private TreeSet<Long> postsIds;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void init() {
        valueOperations = mock(ValueOperations.class);
        postsIds = new TreeSet<>(Arrays.asList(ID_ONE, ID_TWO, ID_FOUR));
        feedCacheDto = FeedCacheDto.builder()
                .subscriberId(ID_ONE)
                .postsIds(postsIds)
                .build();
    }

    @Test
    @DisplayName("Success when save FeedCacheDto in Redis cache")
    public void whenSaveFeedCacheDtoThenSuccess() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations)
                .set(String.valueOf(feedCacheDto.getSubscriberId()), feedCacheDto.getPostsIds());

        feedCacheRepository.save(feedCacheDto);

        verify(redisTemplate.opsForValue())
                .set(String.valueOf(feedCacheDto.getSubscriberId()), feedCacheDto.getPostsIds());
    }

    @Test
    @DisplayName("When finding FeedCacheDto by existing subscriberId")
    public void whenFindBySubscriberIdThenReturnFeedCacheDto() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(String.valueOf(ID_ONE))).thenReturn(feedCacheDto);

        Optional<FeedCacheDto> result = feedCacheRepository.findBySubscriberId(ID_ONE);

        assertTrue(result.isPresent());
        assertEquals(feedCacheDto, result.get());
        verify(redisTemplate.opsForValue()).get(String.valueOf(ID_ONE));
    }

    @Test
    @DisplayName("When finding FeedCacheDto by does not existing subscriberId")
    public void whenFindBySubscriberIdThenReturnOptionalEmpty() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(String.valueOf(ID_ONE))).thenReturn(null);

        Optional<FeedCacheDto> result = feedCacheRepository.findBySubscriberId(ID_ONE);

        assertFalse(result.isPresent());
        verify(redisTemplate.opsForValue()).get(String.valueOf(ID_ONE));
    }

    @Test
    @DisplayName("Success when FeedCacheDtoPostsIds size equals max count of postsIds")
    public void whenAddPostIdWithFeedPostsIdsEqualsToMaxCountThenReturnCorrectDto() {
        when(redisProperties.getMaxPostCountInFeed()).thenReturn(3);

        feedCacheRepository.addPostId(feedCacheDto, ID_THREE);

        assertEquals(3, feedCacheDto.getPostsIds().size());
        assertTrue(feedCacheDto.getPostsIds().contains(ID_THREE));
    }

    @Test
    @DisplayName("Success when FeedCacheDtoPostsIds size exceeds max count of postsIds")
    public void whenAddPostIdWithFeedPostsIdsExceedsMaxCountThenReturnCorrectDto() {
        when(redisProperties.getMaxPostCountInFeed()).thenReturn(2);

        feedCacheRepository.addPostId(feedCacheDto, ID_THREE);

        assertEquals(2, feedCacheDto.getPostsIds().size());
        assertTrue(feedCacheDto.getPostsIds().contains(ID_THREE));
    }

    @Test
    @DisplayName("Success when FeedCacheDtoPostsIds size less then max count of postsIds")
    public void whenAddPostIdWithFeedPostsIdsLessThenMaxCountThenReturnCorrectDto() {
        when(redisProperties.getMaxPostCountInFeed()).thenReturn(4);

        feedCacheRepository.addPostId(feedCacheDto, ID_THREE);

        assertEquals(4, feedCacheDto.getPostsIds().size());
        assertTrue(feedCacheDto.getPostsIds().contains(ID_THREE));
    }
}