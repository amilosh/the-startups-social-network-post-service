package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.filter.PostFilters;
import faang.school.postservice.util.ModerationDictionary;
import faang.school.postservice.validator.post.PostValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
    @Mock
    private ModerationDictionary moderationDictionary;
    @InjectMocks
    private PostService postService;

    @Test
    public void shouldCreatePosts() {
        PostRequestDto postDto = new PostRequestDto();
        postDto.setContent("Test content");

        Post post = new Post();
        post.setContent("Test content");

        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);

        postService.create(postDto);

        verify(postValidator).validateCreate(postDto);
        verify(postMapper).toEntity(postDto);
        verify(postRepository).save(post);

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

        PostResponseDto postDto = new PostResponseDto();
        postDto.setId(postId);

        when(postValidator.validateAndGetPostById(postId)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(updatedPost);
        when(postMapper.toDto(updatedPost)).thenReturn(postDto);


        PostResponseDto result = postService.publishPost(postId);

        verify(postValidator).validatePublish(post);
        verify(postRepository).save(post);
        verify(postMapper).toDto(updatedPost);

        assertEquals(result.getId(), postId);
    }

    @Test
    public void shouldUpdatePost() {

        PostUpdateDto postDto = new PostUpdateDto();
        postDto.setId(1L);
        postDto.setContent("Updated content");

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setContent("Old content");

        Post updatedPost = new Post();
        updatedPost.setId(1L);
        updatedPost.setContent("Updated content");

        when(postValidator.validateAndGetPostById(1L)).thenReturn(existingPost);
        when(postRepository.save(existingPost)).thenReturn(updatedPost);

        PostResponseDto result = postService.updatePost(postDto);

        verify(postValidator).validateAndGetPostById(1L);
        verify(postRepository).save(existingPost);
        verify(postMapper).toDto(updatedPost);

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

        PostResponseDto postDto = new PostResponseDto();
        postDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(updatedPost);

        postService.deletePost(postId);

        verify(postValidator).validateDelete(post);
        verify(postRepository).findById(postId);
        verify(postRepository).save(post);

        assertTrue(updatedPost.isDeleted());
    }

    @Test
    public void shouldReturnPostById() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        PostResponseDto postDto = new PostResponseDto();
        postDto.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostResponseDto result = postService.getPostById(postId);

        verify(postRepository).findById(postId);
        verify(postMapper).toDto(post);

        assertEquals(postId, result.getId());
    }

    @Test
    public void shouldFilterPosts() {
        PostFilterDto filterDto = new PostFilterDto();
        PostFilters filter = mock(PostFilters.class);

        Post post1 = new Post();
        Post post2 = new Post();

        PostResponseDto postDto1 = new PostResponseDto();
        PostResponseDto postDto2 = new PostResponseDto();

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));
        when(postFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(), eq(filterDto))).thenReturn(Stream.of(post1, post2));
        when(postMapper.toDtoList(anyList())).thenReturn(Arrays.asList(postDto1, postDto2));

        List<PostResponseDto> result = postService.getPosts(filterDto);

        verify(postRepository).findAll();
        verify(filter).isApplicable(filterDto);
        verify(filter).apply(any(), eq(filterDto));
        verify(postMapper).toDtoList(anyList());
        assertEquals(2, result.size());
    }

    @Test
    public void testVerifyPostsForModeration() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Test1");
        Post post2 = new Post();
        post1.setId(2L);
        post1.setContent("Test2");
        List<Post> batch = Arrays.asList(post1, post2);

        when(moderationDictionary.isVerified(post1.getContent())).thenReturn(true);
        when(moderationDictionary.isVerified(post2.getContent())).thenReturn(false);

        postService.verifyPostsForModeration(batch);

        assertEquals(LocalDateTime.now().getMinute(), post1.getVerifiedDate().getMinute(), "Verified date should be set");
        assertTrue(post1.getVerified(), "Post 1 should be verified");
        assertEquals(LocalDateTime.now().getMinute(), post2.getVerifiedDate().getMinute(), "Verified date should be set");
        assertFalse(post2.getVerified(), "Post 2 should not be verified");

        verify(postRepository, times(2)).save(any(Post.class));
    }

}
