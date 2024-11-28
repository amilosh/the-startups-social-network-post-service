package faang.school.postservice.service;

import faang.school.postservice.cache.PostCache;
import faang.school.postservice.cache.UserCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.FeedPost;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.FeedPostMapper;
import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.mapper.UserCacheMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaHeatFeedProducer;
import faang.school.postservice.repository.NewsFeedRedisRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsFeedServiceTest {

    @Mock
    private NewsFeedRedisRepository newsFeedRedisRepository;

    @Mock
    private PostRedisRepository postRedisRepository;

    @Mock
    private FeedPostMapper feedPostMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCacheMapper postCacheMapper;

    @Mock
    private UserCacheMapper userCacheMapper;

    @Mock
    private UserRedisRepository userRedisRepository;

    @Mock
    private KafkaHeatFeedProducer kafkaHeatFeedProducer;

    @InjectMocks
    private NewsFeedService newsFeedService;


    @Test
    public void testAllocateToFeeds() {
        Long postId = 1L;
        Long createdAt = System.currentTimeMillis();
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        newsFeedService.allocateToFeeds(postId, createdAt, userIds);

        verify(newsFeedRedisRepository, times(userIds.size())).addPostId(eq(postId), anyLong(), eq(createdAt));
    }

    @Test
    public void testGetFeedBatch_LastPostIdIsNull() {
        Long userId = 100L;
        List<Long> cachedPostIds = Arrays.asList(1L, 2L, 3L);

        PostCache postCache = mock(PostCache.class);
        Optional<PostCache> postCacheOptional = Optional.of(postCache);
        FeedPost feedPost = mock(FeedPost.class);

        ReflectionTestUtils.setField(newsFeedService, "postBatchSize", 3);

        when(newsFeedRedisRepository.getPostIdsFirstBatch(anyLong())).thenReturn(cachedPostIds);
        when(postRedisRepository.findById(anyLong())).thenReturn(postCacheOptional, postCacheOptional, postCacheOptional);
        when(feedPostMapper.toFeedPostsList(List.of(postCache, postCache, postCache)))
                .thenReturn(List.of(feedPost, feedPost, feedPost));

        newsFeedService.getFeedBatch(userId, null);

        verify(newsFeedRedisRepository, never()).getPostIdsBatch(anyLong(), anyLong());
    }

    @Test
    public void testGetFeedBatch_LastPostIdIsNotNullAndPostCacheLessThenPostBatchSize() {
        Long userId = 100L;
        List<Long> cachedPostIds = Arrays.asList(1L, 2L, 3L);

        PostCache postCache = mock(PostCache.class);
        FeedPost feedPost = mock(FeedPost.class);
        Optional<PostCache> postCacheOptional = Optional.of(postCache);

        ReflectionTestUtils.setField(newsFeedService, "postBatchSize", 3);

        when(newsFeedRedisRepository.getPostIdsBatch(eq(userId), anyLong())).thenReturn(cachedPostIds);
        when(postRedisRepository.findById(anyLong())).thenReturn(postCacheOptional, postCacheOptional);
        when(feedPostMapper.toFeedPostsList(anyList())).thenReturn(List.of(feedPost, feedPost, feedPost));

        newsFeedService.getFeedBatch(userId, 1L);

        verify(newsFeedRedisRepository, never()).getPostIdsFirstBatch(userId);
    }

    @Test
    public void testHeatUserFeed() {
        List<Long> usersInPost = new ArrayList<>(List.of(100L, 200L, 300L));

        UserCache userCache = mock(UserCache.class);
        UserDto userDto = mock(UserDto.class);
        Post post = mock(Post.class);
        PostCache postCache = mock(PostCache.class);

        ReflectionTestUtils.setField(newsFeedService, "feedCapacity", 3);

        when(postRepository.findFeedPost(anyList(), eq(Long.MAX_VALUE), anyInt())).thenReturn(List.of(post, post, post));
        when(postCacheMapper.toPostCache(any(Post.class))).thenReturn(postCache, postCache, postCache);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(List.of(userDto, userDto, userDto));
        when(userCacheMapper.toUserCache(any(UserDto.class))).thenReturn(userCache, userCache, userCache);

        newsFeedService.heatUserFeed(400L, usersInPost);

        verify(postRedisRepository, times(3)).save(any(PostCache.class));
        verify(userCacheMapper, times(9)).toUserCache(any(UserDto.class));
        verify(userRedisRepository, times(9)).save(any(UserCache.class));
    }

    @Test
    public void testStartHeat() {
        UserDto userDto = mock(UserDto.class);
        List<UserDto> usersBatch = new ArrayList<>(List.of(userDto, userDto));
        List<UserDto> emptyUsersBatch = new ArrayList<>();

        ReflectionTestUtils.setField(newsFeedService, "userHeatFeedBatchSize", 3);

        when(userServiceClient.getUsersWithFollowings(anyInt(), anyInt())).thenReturn(usersBatch, usersBatch, emptyUsersBatch);

        newsFeedService.startHeat();

        verify(kafkaHeatFeedProducer, times(2)).publish(anyList());
    }
}
