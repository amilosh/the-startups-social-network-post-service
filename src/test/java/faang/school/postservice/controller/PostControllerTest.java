package faang.school.postservice.controller;

import faang.school.postservice.controller.post.PostController;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.service.post.PostService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    private PostDto postDto;

    @BeforeEach
    public void setup() {
        postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("Sample content");
        postDto.setAuthorId(1L);
    }

    @Test
    public void shouldCreatePost() {
        when(postService.create(any(PostDto.class))).thenReturn(postDto);

        PostDto result = postController.createPost(postDto);

        assertEquals(postDto, result);
        verify(postService, times(1)).create(postDto);
    }

    @Test
    public void shouldPublishPost(){
        Long postId = 1L;
        when(postService.publishPost(postId)).thenReturn(postDto);
        PostDto result = postController.publishPost(postId);

        assertEquals(postDto, result);
        verify(postService, times(1)).publishPost(postId);
    }
    @Test
    public void shouldUpdatePost() {
        when(postService.updatePost(any(PostDto.class))).thenReturn(postDto);

        PostDto result = postController.updatePost(postDto);

        assertEquals(postDto, result);
        verify(postService, times(1)).updatePost(postDto);
    }

    @Test
    public void shouldDeletePost() {
        Long postId = 1L;
        postController.deletePost(postId);

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    public void shouldGetPostById() {
        Long postId = 1L;
        when(postService.getPostById(postId)).thenReturn(postDto);

        PostDto result = postController.getPostById(postId);

        assertEquals(postDto, result);
        verify(postService, times(1)).getPostById(postId);
    }

    @Test
    public void shouldGetPosts() {
        PostFilterDto postFilterDto = new PostFilterDto();
        List<PostDto> posts = List.of(postDto);
        when(postService.getPosts(any(PostFilterDto.class))).thenReturn(posts);

        List<PostDto> result = postController.getPost(postFilterDto);

        assertEquals(posts, result);
        verify(postService, times(1)).getPosts(postFilterDto);
    }

}
