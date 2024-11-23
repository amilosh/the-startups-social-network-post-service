package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.filter.PostFilters;
import faang.school.postservice.validator.post.PostValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostValidator postValidator;
    @Mock
    private List<PostFilters> postFilters;
    @InjectMocks
    private PostService postService;

    @Test
    public void shouldCreatePosts() {
        PostDto postDto = new PostDto();
        postDto.setContent("Test content");

        Post post = new Post();
        post.setContent("Test content");

        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.create(postDto);

        verify(postValidator).validateCreate(postDto);
        verify(postMapper).toEntity(postDto);
        verify(postRepository).save(post);

        assertEquals(postDto.getContent(), result.getContent());
        assertFalse(post.isPublished());
        assertFalse(post.isDeleted());
    }

    @Test
    public void shouldPublishPost() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setPublished(true);

        PostDto postDto = new PostDto();
        postDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(updatedPost);
        when(postMapper.toDto(updatedPost)).thenReturn(postDto);


        PostDto result = postService.publishPost(postId);

        verify(postValidator).validatePublish(post);
        verify(postRepository).save(post);
        verify(postMapper).toDto(updatedPost);

        assertEquals(result.getId(), postId);
    }

    @Test
    public void shouldUpdatePost() {

        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("Updated content");

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setContent("Old content");

        Post updatedPost = new Post();
        updatedPost.setId(1L);
        updatedPost.setContent("Updated content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(updatedPost);
        when(postMapper.toDto(updatedPost)).thenReturn(postDto);

        PostDto result = postService.updatePost(postDto);

        verify(postValidator).validateUpdate(postDto);
        verify(postRepository).findById(1L);
        verify(postRepository).save(existingPost);
        verify(postMapper).toDto(updatedPost);

        assertEquals("Updated content", result.getContent());
    }

    @Test
    public void shouldDeletePost() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(true);
        post.setDeleted(false);

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setPublished(false);
        updatedPost.setDeleted(true);

        PostDto postDto = new PostDto();
        postDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(updatedPost);
        when(postMapper.toDto(updatedPost)).thenReturn(postDto);

        PostDto result = postService.deletePost(postId);

        verify(postValidator).validateDelete(post);
        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        verify(postMapper).toDto(updatedPost);

        assertEquals(result.getId(), postId);
        assertTrue(updatedPost.isDeleted());
    }

    @Test
    public void shouldReturnPostById() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        PostDto postDto = new PostDto();
        postDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.getPostById(postId);

        verify(postRepository).findById(postId);
        verify(postMapper).toDto(post);

        assertEquals(postId, result.getId());
    }

    @Test
    public void shouldFilterPosts() {

        PostFilterDto filterDto = new PostFilterDto();

        Post post1 = new Post();
        Post post2 = new Post();

        PostDto postDto1 = new PostDto();
        PostDto postDto2 = new PostDto();

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));
        when(postMapper.toDtoList(anyList())).thenReturn(Arrays.asList(postDto1, postDto2));

        List<PostDto> result = postService.getPosts(filterDto);

        verify(postRepository).findAll();
        verify(postMapper).toDtoList(anyList());
        assertEquals(2, result.size());
    }
}
