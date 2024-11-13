package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Spy
    private PostMapper postMapper = new PostMapperImpl();

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getAllPostByUserIdTest() {
        long id = 1L;
        List<Like> likes = new ArrayList<>(List.of(new Like(), new Like()));
        Post newestPost = Post.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45))
                .published(true)
                .deleted(false)
                .likes(likes)
                .build();
        Post olderPost = Post.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45).minusMinutes(1))
                .published(true)
                .deleted(false)
                .build();
        Post notPublishedPost = Post.builder()
                .published(false)
                .deleted(false)
                .build();
        Post deletedPost = Post.builder()
                .deleted(true)
                .published(true)
                .build();

        List<Post> posts = new ArrayList<>(List.of(olderPost, newestPost, notPublishedPost, deletedPost));

        PostDto newestPostDto = PostDto.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45))
                .published(true)
                .deleted(false)
                .likesCount(2)
                .build();
        PostDto olderPostDto = PostDto.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45).minusMinutes(1))
                .published(true)
                .deleted(false)
                .build();
        PostDto notPublishedPostDto = PostDto.builder()
                .published(false)
                .deleted(false)
                .build();
        PostDto deletedPostDto = PostDto.builder()
                .published(true)
                .deleted(true)
                .build();
        when(postRepository.findByAuthorIdWithLikes(id)).thenReturn(posts);

        List<PostDto> result = postService.getAllPostByUserId(id);

        verify(postRepository, times(1)).findByAuthorIdWithLikes(id);
        assertEquals(2, result.size());
        assertEquals(newestPostDto, result.get(0));
        assertEquals(olderPostDto, result.get(1));
        assertEquals(2, result.get(0).getLikesCount());
        assertFalse(result.contains(notPublishedPostDto));
        assertFalse(result.contains(deletedPostDto));
    }

    @Test
    public void getAllPostByProjectIdTest(){
        long id = 1L;
        List<Like> likes = new ArrayList<>(List.of(new Like(), new Like()));
        Post newestPost = Post.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45))
                .published(true)
                .deleted(false)
                .likes(likes)
                .build();
        Post olderPost = Post.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45).minusMinutes(1))
                .published(true)
                .deleted(false)
                .build();
        Post notPublishedPost = Post.builder()
                .published(false)
                .deleted(false)
                .build();
        Post deletedPost = Post.builder()
                .deleted(true)
                .published(true)
                .build();

        List<Post> posts = new ArrayList<>(List.of(olderPost, newestPost, notPublishedPost, deletedPost));

        PostDto newestPostDto = PostDto.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45))
                .published(true)
                .deleted(false)
                .likesCount(2)
                .build();
        PostDto olderPostDto = PostDto.builder()
                .publishedAt(LocalDateTime.of(2023, 11, 13, 14, 30, 45).minusMinutes(1))
                .published(true)
                .deleted(false)
                .build();
        PostDto notPublishedPostDto = PostDto.builder()
                .published(false)
                .deleted(false)
                .build();
        PostDto deletedPostDto = PostDto.builder()
                .published(true)
                .deleted(true)
                .build();
        when(postRepository.findByProjectIdWithLikes(id)).thenReturn(posts);

        List<PostDto> result = postService.getAllPostByProjectId(id);

        verify(postRepository, times(1)).findByProjectIdWithLikes(id);
        assertEquals(2, result.size());
        assertEquals(newestPostDto, result.get(0));
        assertEquals(olderPostDto, result.get(1));
        assertEquals(2, result.get(0).getLikesCount());
        assertFalse(result.contains(notPublishedPostDto));
        assertFalse(result.contains(deletedPostDto));
    }

    @Test
    public void getPostEntityThrowExceptionTest() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPostEntity(postId));
    }

    @Test
    public void getPostEntityTest() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Post result = postService.getPostEntity(postId);

        verify(postRepository, times(1)).findById(postId);
        assertEquals(postId, result.getId());
        assertEquals(post, result);
    }

    @Test
    public void addLikeToPostTest() {
        long id = 1L;
        Post post = Post.builder()
                .id(id).build();
        Like like = Like.builder()
                .id(id).build();
        List<Like> likes = new ArrayList<>();
        post.setLikes(likes);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        postService.addLikeToPost(post.getId(), like);

        verify(postRepository).save(post);
        assertTrue(post.getLikes().contains(like));
    }

    @Test
    public void removeLikeFromPostTest() {
        long id = 1L;
        Post post = Post.builder()
                .id(id).build();
        Like like = Like.builder()
                .id(id).build();
        List<Like> likes = new ArrayList<>(List.of(like));
        post.setLikes(likes);
        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        postService.removeLikeFromPost(post.getId(), like);

        verify(postRepository).save(post);
        assertFalse(post.getLikes().contains(like));
    }
}