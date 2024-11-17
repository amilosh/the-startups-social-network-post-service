package faang.school.postservice.service;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private PostValidator validator;

    private Post post;
    private PostDto postDto;
    private ProjectDto projectDto;
    private UserDto userDto;
    private List<Post> postList;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(1L);
        post.setCreatedAt(LocalDateTime.MIN);
        post.setPublishedAt(LocalDateTime.now());
        userDto = UserDto.builder()
                .id(1L)
                .username("John")
                .email("john@example.com")
                .build();
        projectDto = ProjectDto.builder()
                .id(1L)
                .build();
        postDto = PostDto.builder()
                .authorId(1L)
                .build();
        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setAuthorId(2L);
        secondPost.setCreatedAt(LocalDateTime.now());
        secondPost.setPublishedAt(LocalDateTime.now());
        postList = List.of(post, secondPost);

    }

    @Test
    public void testCreatePost() {
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(userServiceClient.getUser(userDto.id())).thenReturn(userDto);
        when(postRepository.save(post)).thenReturn(post);

        postService.createPost(postDto);

        verify(postMapper, times(1)).toEntity(postDto);
        verify(postMapper, times(1)).toDto(post);
        verify(postRepository, times(1)).save(post);
        verify(userServiceClient, times(1)).getUser(userDto.id());
    }

    @Test
    public void testCreatePostWithoutAuthor() {
        postDto = PostDto.builder().build();
        assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void testCreatePostWithProjectAndUserAuthor() {
        postDto = PostDto.builder()
                .projectId(1L)
                .authorId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void testPublishPost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.publishPost(1L);

        verify(postRepository, times(1)).findById(anyLong());
        assertTrue(dto.published());
    }

    @Test
    public void testPublishPublishedPost() {
        post.setPublished(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertThrows(DataValidationException.class, () -> postService.publishPost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testPublishDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertThrows(DataValidationException.class, () -> postService.publishPost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdatePost() {
        post.setContent("content");
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .id(1L)
                .content("new content")
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.updatePost(updatePostDto);

        verify(postRepository, times(1)).findById(anyLong());
        assertEquals(dto.content(), updatePostDto.content());
    }

    @Test
    public void testUpdateDeletedPost() {
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .id(1L)
                .content("new content")
                .build();
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.updatePost(updatePostDto));

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testDeletePost() {
        post.setDeleted(false);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.deletePost(1L);

        verify(postRepository, times(1)).findById(anyLong());
        assertTrue(dto.deleted());
    }

    @Test
    public void testDeleteDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.deletePost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetAllDraftNotDeletedPostsByUserId() {
        when(postRepository.findByAuthorId(anyLong())).thenReturn(postList);

        List<PostDto> dtoList = postService.getAllDraftNotDeletedPostsByUserId(1L);

        verify(postRepository, times(1)).findByAuthorId(anyLong());
        assertTrue(dtoList.stream()
                .allMatch(dto -> !dto.published() && !dto.deleted()));
    }

    @Test
    public void testGetAllPublishedNotDeletedPostsByUserId() {
        postList.forEach(p -> p.setPublished(true));
        when(postRepository.findByAuthorId(anyLong())).thenReturn(postList);

        List<PostDto> dtoList = postService.getAllPublishedNotDeletedPostsByUserId(1L);

        verify(postRepository, times(1)).findByAuthorId(anyLong());
        assertTrue(dtoList.stream()
                .allMatch(dto -> dto.published() && !dto.deleted()));
    }
}
