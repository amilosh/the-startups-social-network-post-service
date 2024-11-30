package faang.school.postservice.service.post;

import faang.school.postservice.config.api.SpellingConfig;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.filter.PostFilters;
import faang.school.postservice.validator.post.PostValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
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
    private RestTemplate restTemplate;

    @Mock
    private SpellingConfig api;
    @Mock
    private List<PostFilters> postFilters;
    @InjectMocks
    private PostService postService;

    private Post post;
    private PostRequestDto postDto;
    private PostResponseDto postResponseDto;
    private List<Post> preparedPosts;

    @BeforeEach
    void setUp() {
        postDto = new PostRequestDto();

        // 1 опубликован 3 (1 из них удалён) не опубликовано
        // 1 удалён 3 не удалено
        preparedPosts = new ArrayList<>();

        post = new Post();
        post.setId(1L);
        post.setContent("This is errror");
        postDto.setAuthorId(1L);
        post.setLikes(List.of(new Like(), new Like(), new Like()));
        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now().plusDays(1));
        preparedPosts.add(post);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setLikes(List.of(new Like(), new Like()));
        post2.setPublished(true);
        post2.setDeleted(false);
        post2.setCreatedAt(LocalDateTime.now().plusDays(2));
        preparedPosts.add(post2);

        Post post3 = new Post();
        post3.setId(3L);
        post3.setLikes(List.of(new Like(), new Like(), new Like()));
        post3.setPublished(false);
        post3.setDeleted(false);
        post3.setCreatedAt(LocalDateTime.now().plusDays(3));
        preparedPosts.add(post3);

        Post post4 = new Post();
        post4.setId(4L);
        post4.setLikes(List.of(new Like(), new Like()));
        post4.setPublished(false);
        post4.setDeleted(true);
        post4.setCreatedAt(LocalDateTime.now().plusDays(4));
        preparedPosts.add(post4);
        postResponseDto = new PostResponseDto();
        postResponseDto.setId(1L);
        postResponseDto.setAuthorId(1L);
    }

    @Test
    void testCheckSpellingSuccess() throws InterruptedException {
        String prepareDate = "{\"elements\":[{\"id\":0,\"errors\":[{\"suggestions\":" +
                "[\"error\",\"Rorer\",\"eerier\",\"arrear\",\"rower\",\"Euro\",\"rehear\",\"err\",\"ROR\",\"Orr\"]" +
                ",\"position\":8,\"word\":\"errror\"}]}],\"spellingErrorCount\":1}";
        List<Post> posts = List.of(post);
        when(postRepository.findByPublishedFalse()).thenReturn(posts);
        when(api.getKey()).thenReturn("key");
        when(api.getEndpoint()).thenReturn("endpoint");
        when(restTemplate.postForObject(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(prepareDate);

        postService.checkSpelling();

        verify(postRepository, times(1)).findByPublishedFalse();
        verify(api, times(1)).getKey();
        verify(api, times(1)).getEndpoint();
        verify(postRepository, times(1)).save(post);
        Thread.sleep(200);
        assertEquals("This is error", posts.get(0).getContent());
    }

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
}
