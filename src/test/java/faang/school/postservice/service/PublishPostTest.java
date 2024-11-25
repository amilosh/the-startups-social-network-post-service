package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.mapper.UserWithFollowersMapper;
import faang.school.postservice.model.dto.UserWithFollowersDto;
import faang.school.postservice.redis.publisher.PostViewPublisher;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.impl.PostServiceImpl;
import faang.school.postservice.service.impl.RedisPostServiceImpl;
import faang.school.postservice.service.impl.RedisUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublishPostTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    UserContext userContext;

    @Mock
    PostViewPublisher postViewPublisher;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private UserShortInfoRepository userShortInfoRepository;

    @Mock
    private RedisUserServiceImpl redisUserService;

    @Mock
    private UserWithFollowersMapper userWithFollowersMapper;

    @Mock
    private RedisPostDtoMapper redisPostDtoMapper;

    @Mock
    private RedisPostServiceImpl redisPostService;

    @InjectMocks
    private PostServiceImpl postService;

    private Post unpublishedPost;
    private Post publishedPost;
    private PostDto publishedPostDto;
    private UserWithFollowersDto userWithFollowersDto;

    @BeforeEach
    void setUp() {
        unpublishedPost = new Post();
        unpublishedPost.setId(1L);
        unpublishedPost.setPublished(false);
        unpublishedPost.setContent("Here is the unpublished post");
        unpublishedPost.setAuthorId(1L);

        publishedPost = new Post();
        publishedPost.setId(2L);
        publishedPost.setPublished(true);
        publishedPost.setContent("Here is the already published post");
        publishedPost.setPublishedAt(LocalDateTime.now());
        publishedPost.setAuthorId(1L);

        publishedPostDto = new PostDto();
        publishedPostDto.setId(1L);
        publishedPostDto.setPublished(true);
        publishedPostDto.setContent("Here is the published post");

        userWithFollowersDto = new UserWithFollowersDto();
        userWithFollowersDto.setUserId(1L);
        userWithFollowersDto.setFollowerIds(List.of(2L, 3L));
    }

    @Test
    void shouldPublishPostSuccessfully() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(unpublishedPost));
        when(userServiceClient.getUserWithFollowers(1L)).thenReturn(userWithFollowersDto);
        when(postRepository.save(any(Post.class))).thenAnswer(i -> {
            Post savedPost = i.getArgument(0);
            unpublishedPost.setPublished(savedPost.isPublished());
            unpublishedPost.setPublishedAt(savedPost.getPublishedAt());
            return savedPost;
        });

        lenient().when(postMapper.toPostDto(any(Post.class))).thenReturn(publishedPostDto);

        PostDto result = postService.publishPost(1L);

        assertNotNull(result);
        assertTrue(result.isPublished());
        assertNotNull(unpublishedPost.getPublishedAt());

        verify(postRepository).save(argThat(post -> post.isPublished() && post.getId() == 1L));

        assertTrue(unpublishedPost.isPublished());
    }

    @Test
    void shouldThrowExceptionWhenPostAlreadyPublished() {
        when(postRepository.findById(2L)).thenReturn(java.util.Optional.of(publishedPost));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            postService.publishPost(2L);
        });

        assertEquals("Post is already published", exception.getMessage());
        verify(postRepository, never()).save(publishedPost);
    }
}