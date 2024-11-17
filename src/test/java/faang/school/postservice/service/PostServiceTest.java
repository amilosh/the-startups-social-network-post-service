package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    long postId;
    Post post;

    @BeforeEach
    public void setUp(){
        postId = 5L;

        post = Post.builder()
                .id(postId)
                .build();
    }

    @Test
    public void testGetPostByIdWithExistentPost(){
        when(postRepository.findById(postId))
                .thenReturn(Optional.ofNullable(post));

        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    public void testGetPostByIdWhenPostNotExist(){
        when(postRepository.findById(postId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPostById(postId));
    }

    @Test
    public void testIsPostNotExistWithExistentPost(){
        when(postRepository.existsById(postId)).thenReturn(true);

        boolean result = postService.isPostNotExist(postId);

        assertFalse(result);
    }

    @Test
    public void testIsPostNotExistWhenPostNotExist() {
        when(postRepository.existsById(postId)).thenReturn(false);

        boolean result = postService.isPostNotExist(postId);

        assertTrue(result);
    }
}