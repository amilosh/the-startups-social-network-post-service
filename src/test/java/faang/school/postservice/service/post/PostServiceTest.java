package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.moderation.Verifiable;
import faang.school.postservice.publisher.redisPublisher.post.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для PostService")
public class PostServiceTest {

    private final long authorId = 1L;
    private final long projectId = 1L;
    private static final long ID = 1L;
    private Post post;
    private PostResponseDto postResponseDto;
    private PostRequestDto postRequestDto;
    private List<Long> subscribers;

    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;
    private static final String CONTENT = "content";
    private static final String SWEAR_CONTENT = "bug";
    private static final int THREAD_COUNT = 5;
    private static final long SUBLIST_LENGTH = 10L;

    private Post firstPost;
    private Post secondPost;
    private Post verifiedFirstPost;
    private Post verifiedSecondPost;
    private List<Post> unverifiedPosts;
    private List<Post> verifiedPosts;
    private List<Verifiable> unverifiables;

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostViewEventPublisher postViewEventPublisher;

    @Mock
    private UserContext userContext;

    @Mock
    private List<Post> posts;

    @Mock
    private Sender sender;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private Executor executor;

    @BeforeEach
    public void setup() {
        post = new Post();
        post.setId(ID);
        post.setContent("Test content");
        post.setAuthorId(authorId);
        post.setProjectId(projectId);
        post.setLikes(Collections.emptyList());
        post.setPublishedAt(LocalDateTime.now());

        postResponseDto = new PostResponseDto(post.getId(),
                post.getContent(),
                post.getAuthorId(),
                post.getProjectId(),
                0,
                post.getPublishedAt());

        postRequestDto = PostRequestDto.builder()
                .content("Test content")
                .authorId(authorId)
                .authorId(authorId)
                .build();

        ReflectionTestUtils.setField(postService, "sublistLength", SUBLIST_LENGTH);
        ReflectionTestUtils.setField(postService, "executor", Executors.newFixedThreadPool(THREAD_COUNT));

        firstPost = Post.builder()
                .id(ID_ONE)
                .content(CONTENT)
                .build();

        secondPost = Post.builder()
                .id(ID_TWO)
                .content(SWEAR_CONTENT)
                .build();

        verifiedFirstPost = Post.builder()
                .id(ID_ONE)
                .content(CONTENT)
                .verified(true)
                .build();

        verifiedSecondPost = Post.builder()
                .id(ID_TWO)
                .content(SWEAR_CONTENT)
                .verified(false)
                .build();

        unverifiedPosts = List.of(firstPost, secondPost);
        verifiedPosts = List.of(verifiedFirstPost, verifiedSecondPost);
        unverifiables = List.of(firstPost, secondPost);
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTests {

        @Test
        @DisplayName("When post exists then return post response dto")
        void whenPostIdIsPositiveAndExistsThenReturnPostResponseDto() {
            userContext.setUserId(ID);
            when(postRepository.findById(ID))
                    .thenReturn(Optional.of(post));
            when(postMapper.toResponseDto(eq(post), anyInt()))
                    .thenReturn(postResponseDto);

            postService.getPost(ID);

            verify(postRepository).findById(ID);
            verify(postMapper).toResponseDto(eq(post), anyInt());
            verify(postViewEventPublisher).publish(argThat(event ->
                    event.getPostId() == ID &&
                            event.getAuthorId().equals(ID)));
        }

        @Test
        @DisplayName("When get all posts not published then success")
        void whenGetAllPostsNotPublishedThenSuccess() {
            when(postRepository.findReadyToPublish()).thenReturn(posts);

            List<Post> postList = postService.getAllPostsNotPublished();

            assertEquals(postList, posts);
            verify(postRepository, atLeastOnce()).findReadyToPublish();
        }

        @Test
        @DisplayName("Должен вернуть посты автора с количеством лайков")
        void shouldReturnPostsByAuthorWithLikes() {
            when(postRepository.findByAuthorIdWithLikes(authorId)).thenReturn(List.of(post));
            when(postMapper.toResponseDto(post, 0)).thenReturn(postResponseDto);

            List<PostResponseDto> result = postService.getPostsByAuthorWithLikes(authorId);

            assertEquals(1, result.size());
            assertEquals(postResponseDto, result.get(0));

            verify(postRepository).findByAuthorIdWithLikes(authorId);
            verify(postMapper).toResponseDto(post, 0);
        }

        @Test
        @DisplayName("When Post ID is valid then return the Post")
        void whenFindByIdThenSuccess() {
            when(postRepository.findById(ID)).thenReturn(Optional.of(post));

            Post existedPost = postService.findById(ID);

            assertNotNull(existedPost);
            assertEquals(post.getId(), existedPost.getId());
            verify(postRepository).findById(ID);
        }

        @Test
        @DisplayName("When Post ID is invalid then throw EntityNotFoundException")
        void whenFindByIdThenThrowException() {
            when(postRepository.findById(ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> postService.findById(ID));
        }

        @Test
        @DisplayName("Должен вернуть посты проекта с количеством лайков")
        void shouldReturnPostsByProjectWithLikes() {
            when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(post));
            when(postMapper.toResponseDto(post, 0)).thenReturn(postResponseDto);

            List<PostResponseDto> result = postService.getPostsByProjectWithLikes(projectId);

            assertEquals(1, result.size());
            assertEquals(postResponseDto, result.get(0));

            verify(postRepository).findByProjectIdWithLikes(projectId);
            verify(postMapper).toResponseDto(post, 0);
        }

        @Test
        void testCreatePost_WhenArgsValid_ReturnPostResponseDto() {
            when(postMapper.toPost(postRequestDto)).thenReturn(post);
            when(postRepository.save(post)).thenReturn(post);
            when(postMapper.toResponseDto(post, 0)).thenReturn(postResponseDto);

            PostResponseDto result = postService.createPost(postRequestDto);

            assertNotNull(result);
            verify(postMapper).toPost(postRequestDto);
            verify(postRepository).save(post);
            verify(sender).batchSending(post);
            verify(postMapper).toResponseDto(any(Post.class), anyInt());
        }

        @Test
        @DisplayName("Успешный вызов метода moderationPostContent")
        void whenModeratePostsContentThenSuccess() {
            when(postRepository.findReadyToVerified()).thenReturn(unverifiedPosts);
            when(postRepository.saveAll(verifiedPosts)).thenReturn(verifiedPosts);

            postService.moderatePostsContent();

            CompletableFuture<Void> allTasks = CompletableFuture.completedFuture(null);
            unverifiedPosts.forEach(post ->
                    allTasks.thenRunAsync(() -> moderationDictionary.searchSwearWords(unverifiedPosts), executor)
            );

            allTasks.join();

            verify(postRepository).findReadyToVerified();
            verify(moderationDictionary).searchSwearWords(anyList());
            verify(postRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("Негативные тесты")
    class NegativeTests {

        @Test
        @DisplayName("Должен вернуть пустой список, если у автора нет постов")
        void shouldReturnEmptyListIfNoPostsForAuthor() {
            when(postRepository.findByAuthorIdWithLikes(authorId)).thenReturn(Collections.emptyList());

            List<PostResponseDto> result = postService.getPostsByAuthorWithLikes(authorId);

            assertEquals(0, result.size());

            verify(postRepository).findByAuthorIdWithLikes(authorId);
            verify(postMapper, never()).toResponseDto(any(), anyInt());
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если у проекта нет постов")
        void shouldReturnEmptyListIfNoPostsForProject() {
            when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(Collections.emptyList());

            List<PostResponseDto> result = postService.getPostsByProjectWithLikes(projectId);

            assertEquals(0, result.size());

            verify(postRepository).findByProjectIdWithLikes(projectId);
            verify(postMapper, never()).toResponseDto(any(), anyInt());
        }

        @Test
        @DisplayName("When post not exists then throw Exception")
        void whenPostNotExistsThenThrowException() {
            when(postRepository.findById(ID))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> postService.getPost(ID));
        }
    }
}
