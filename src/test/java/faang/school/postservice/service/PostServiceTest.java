package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.post.response.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper;

    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostService postService;

    long userId = 1L;

    Post firstPost = new Post();
    Post secondPost = new Post();

    PostDto firstPostDto = new PostDto();
    PostDto secondPostDto = new PostDto();

    @Test
    void createShouldCreatePostSuccessfully() {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("Valid content");
        createPostDto.setAuthorId(1L);
        createPostDto.setProjectId(2L);

        Post postEntity = new Post();
        postEntity.setId(1);
        postEntity.setAuthorId(createPostDto.getAuthorId());
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setScheduledAt(LocalDateTime.now());
        postEntity.setPublished(false);
        postEntity.setDeleted(false);

        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent(createPostDto.getContent());

        doNothing().when(postValidator).validateContent(createPostDto.getContent());
        doNothing().when(postValidator).validateAuthorIdAndProjectId(createPostDto.getAuthorId(), createPostDto.getProjectId());
        doNothing().when(postValidator).validateAuthorId(createPostDto.getAuthorId());
        doNothing().when(postValidator).validateProjectId(createPostDto.getProjectId());
        when(postMapper.toEntity(createPostDto)).thenReturn(postEntity);
        when(postRepository.save(postEntity)).thenReturn(postEntity);
        when(postMapper.toDto(postEntity)).thenReturn(postDto);

        PostDto result = postService.create(createPostDto);

        assertNotNull(result);
        assertEquals(postDto.getId(), result.getId());
        assertEquals(postDto.getContent(), result.getContent());
        assertFalse(postEntity.isPublished());
        assertFalse(postEntity.isDeleted());

        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void createShouldThrowExceptionWhenContentIsInvalid() {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("");
        createPostDto.setAuthorId(1L);
        createPostDto.setProjectId(2L);

        doThrow(new DataValidationException("Content cannot be blank")).when(postValidator).validateContent(createPostDto.getContent());

        assertThrows(DataValidationException.class, () -> postService.create(createPostDto));

        verify(postValidator, times(1)).validateContent(createPostDto.getContent());
    }

    @Test
    void publishShouldSetPublishedAndReturnPostDto() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);

        PostDto expectedDto = new PostDto();
        expectedDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedDto);

        PostDto result = postService.publish(postId);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validatePostIdOnPublished(postId);

        assertEquals(true, post.isPublished());
        assertEquals(expectedDto, result);

        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void updateShouldUpdateContentAndReturnPostDto() {
        Long postId = 1L;
        String newContent = "Updated content";
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setContent(newContent);

        Post post = new Post();
        post.setId(postId);
        post.setContent("Old content");

        PostDto expectedDto = new PostDto();
        expectedDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(any(Post.class))).thenReturn(expectedDto);

        PostDto result = postService.update(postId, updatePostDto);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validateContent(newContent);

        assertEquals(newContent, post.getContent());
        assertEquals(expectedDto, result);

        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void deleteShouldMarkPostAsDeletedAndSave() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.delete(postId);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validatePostIdOnRemoved(postId);

        assert(post.isDeleted());

        verify(postRepository, times(1)).save(post);
    }

    @Test
    void getByIdShouldValidateAndReturnPostDto() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        PostDto postDto = new PostDto();
        postDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.getById(postId);

        verify(postValidator, times(1)).validateExistingPostId(postId);
        verify(postValidator, times(1)).validatePostIdOnRemoved(postId);
        verify(postRepository, times(1)).findById(postId);
        verify(postMapper, times(1)).toDto(post);

        assertEquals(postDto, result);
    }

    @Test
    void getDraftByUserIdShouldValidateAndReturnDraftPosts() {
        when(postRepository.findReadyToPublishByAuthor(userId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstPostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondPostDto);

        List<PostDto> result = postService.getDraftByUserId(userId);

        verify(postValidator, times(1)).validateAuthorId(userId);
        verify(postRepository, times(1)).findReadyToPublishByAuthor(userId);

        assertEquals(List.of(firstPostDto, secondPostDto), result);
    }

    @Test
    void getDraftByProjectIdShouldValidateAndReturnDraftPosts() {
        when(postRepository.findReadyToPublishByProject(userId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstPostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondPostDto);

        List<PostDto> result = postService.getDraftByProjectId(userId);

        verify(postValidator, times(1)).validateAuthorId(userId);
        verify(postRepository, times(1)).findReadyToPublishByProject(userId);

        assertEquals(List.of(firstPostDto, secondPostDto), result);
    }

    @Test
    void getPublishedByUserIdShouldValidateAndReturnPublishedPosts() {
        when(postRepository.findPublishedByAuthor(userId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstPostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondPostDto);

        List<PostDto> result = postService.getPublishedByUserId(userId);

        verify(postValidator, times(1)).validateAuthorId(userId);
        verify(postRepository, times(1)).findPublishedByAuthor(userId);

        assertEquals(List.of(firstPostDto, secondPostDto), result);
    }

    @Test
    void getPublishedByProjectIdShouldValidateAndReturnPublishedPosts() {
        Long projectId = 1L;

        when(postRepository.findPublishedByProject(projectId)).thenReturn(List.of(firstPost, secondPost));
        when(postMapper.toDto(firstPost)).thenReturn(firstPostDto);
        when(postMapper.toDto(secondPost)).thenReturn(secondPostDto);

        List<PostDto> result = postService.getPublishedByProjectId(projectId);

        verify(postValidator, times(1)).validateProjectId(projectId);
        verify(postRepository, times(1)).findPublishedByProject(projectId);

        assertEquals(List.of(firstPostDto, secondPostDto), result);
    }
}