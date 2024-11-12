package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostService postService;

    @Captor
    private ArgumentCaptor<Post> captor;

    @Test
    public void createPostTest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setAuthorId(1L);
        postDto.setContent("Hello world!");
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        when(postRepository.save(captor.capture())).thenReturn(post);

        postService.createPost(postDto);

        Post createPost = captor.getValue();

        assertEquals(postDto.getAuthorId(), createPost.getAuthorId());
        assertEquals(postDto.getContent(), createPost.getContent());
        assertFalse(createPost.isPublished());
        assertFalse(createPost.isDeleted());
    }

    @Test
    public void publishPostTest() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        post.setPublished(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(captor.capture())).thenReturn(post);

        postService.publishPost(1L);

        Post createPost = captor.getValue();

        assertEquals(post.getAuthorId(), createPost.getAuthorId());
        assertEquals(post.getContent(), createPost.getContent());
        assertTrue(createPost.isPublished());
    }

    @Test
    public void republishPostTest() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        post.setPublished(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(PostException.class, () -> postService.publishPost(1L));
    }

    @Test
    public void updatePostTest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setAuthorId(1L);
        postDto.setContent("Bye world!");
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(captor.capture())).thenReturn(post);

        postService.updatePost(postDto);

        Post updatePost = captor.getValue();

        assertEquals(postDto.getAuthorId(), updatePost.getAuthorId());
        assertEquals(postDto.getContent(), updatePost.getContent());
    }

    @Test
    public void deletePostTest() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        post.setDeleted(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(captor.capture())).thenReturn(post);

        postService.deletePostById(1L);

        Post deletePost = captor.getValue();

        assertTrue(deletePost.isDeleted());
        assertEquals(post.getAuthorId(), deletePost.getAuthorId());
        assertEquals(post.getContent(), deletePost.getContent());
    }

    @Test
    public void getPostByIdTest() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostDto postDto = postService.getPostById(1L);

        assertEquals(post.getId(), postDto.getId());
        assertEquals(post.getAuthorId(), postDto.getAuthorId());
        assertEquals(post.getContent(), postDto.getContent());
    }

    @Test
    public void getNoExistPostByIdTest() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Hello world!");
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostException.class, () -> postService.getPostById(1L));
    }

    @Test
    public void getAllNoPublishPostByUserIdTest() {
        Post firstPost = new Post();
        firstPost.setId(1L);
        firstPost.setAuthorId(1L);
        firstPost.setContent("Hello world!");
        firstPost.setPublished(false);
        firstPost.setDeleted(false);
        firstPost.setCreatedAt(LocalDateTime.now());
        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setAuthorId(1L);
        secondPost.setContent("Bye world!");
        secondPost.setPublished(false);
        secondPost.setDeleted(false);
        secondPost.setCreatedAt(LocalDateTime.now().plusSeconds(1));
        Post thirdPost = new Post();
        thirdPost.setId(3L);
        thirdPost.setAuthorId(1L);
        thirdPost.setContent("Bye, bye!");
        thirdPost.setPublished(true);
        thirdPost.setDeleted(false);
        thirdPost.setCreatedAt(LocalDateTime.now().plusSeconds(2));
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> posts = postService.getAllNoPublishPostByUserId(1L);

        assertEquals(2, posts.size());
        assertEquals(firstPost.getId(), posts.get(1).getId());
        assertEquals(secondPost.getId(), posts.get(0).getId());
    }

    @Test
    public void getAllNoPublishPostByNoExistUserTest() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of());

        List<PostDto> posts = postService.getAllNoPublishPostByUserId(1L);

        assertEquals(0, posts.size());
    }

    @Test
    public void getAllNoPublishPostByProjectIdTest() {
        Post firstPost = new Post();
        firstPost.setId(1L);
        firstPost.setProjectId(1L);
        firstPost.setContent("Hello world!");
        firstPost.setPublished(false);
        firstPost.setDeleted(false);
        firstPost.setCreatedAt(LocalDateTime.now());
        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setProjectId(1L);
        secondPost.setContent("Bye world!");
        secondPost.setPublished(false);
        secondPost.setDeleted(false);
        secondPost.setCreatedAt(LocalDateTime.now().plusSeconds(1));
        Post thirdPost = new Post();
        thirdPost.setId(3L);
        thirdPost.setProjectId(1L);
        thirdPost.setContent("Bye, bye!");
        thirdPost.setPublished(true);
        thirdPost.setDeleted(false);
        thirdPost.setCreatedAt(LocalDateTime.now().plusSeconds(2));
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> posts = postService.getAllNoPublishPostByProjectId(1L);

        assertEquals(2, posts.size());
        assertEquals(firstPost.getId(), posts.get(1).getId());
        assertEquals(secondPost.getId(), posts.get(0).getId());
    }

    @Test
    public void getAllPostByUserIdTest() {
        Post firstPost = new Post();
        firstPost.setId(1L);
        firstPost.setAuthorId(1L);
        firstPost.setContent("Hello world!");
        firstPost.setPublished(false);
        firstPost.setDeleted(false);
        firstPost.setPublishedAt(LocalDateTime.now());
        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setAuthorId(1L);
        secondPost.setContent("Bye world!");
        secondPost.setPublished(true);
        secondPost.setDeleted(false);
        secondPost.setPublishedAt(LocalDateTime.now().plusSeconds(1));
        Post thirdPost = new Post();
        thirdPost.setId(3L);
        thirdPost.setAuthorId(1L);
        thirdPost.setContent("Bye, bye!");
        thirdPost.setPublished(true);
        thirdPost.setDeleted(false);
        thirdPost.setPublishedAt(LocalDateTime.now().plusSeconds(2));
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> posts = postService.getAllPostByUserId(1L);

        assertEquals(2, posts.size());
        assertEquals(secondPost.getId(), posts.get(1).getId());
        assertEquals(thirdPost.getId(), posts.get(0).getId());
    }

    @Test
    public void getAllPostByProjectIdTest() {
        Post firstPost = new Post();
        firstPost.setId(1L);
        firstPost.setProjectId(1L);
        firstPost.setContent("Hello world!");
        firstPost.setPublished(false);
        firstPost.setDeleted(false);
        firstPost.setPublishedAt(LocalDateTime.now());
        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setProjectId(1L);
        secondPost.setContent("Bye world!");
        secondPost.setPublished(true);
        secondPost.setDeleted(false);
        secondPost.setPublishedAt(LocalDateTime.now().plusSeconds(1));
        Post thirdPost = new Post();
        thirdPost.setId(3L);
        thirdPost.setProjectId(1L);
        thirdPost.setContent("Bye, bye!");
        thirdPost.setPublished(true);
        thirdPost.setDeleted(false);
        thirdPost.setPublishedAt(LocalDateTime.now().plusSeconds(2));
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> posts = postService.getAllPostByProjectId(1L);

        assertEquals(2, posts.size());
        assertEquals(secondPost.getId(), posts.get(1).getId());
        assertEquals(thirdPost.getId(), posts.get(0).getId());
    }
}
