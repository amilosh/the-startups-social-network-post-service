package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post post;

    @BeforeEach
    void setUp(){
        post = createTestPost();
    }

    @Test
    @DisplayName("Get post with valid id")
    void testGetPostByIdValidId() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(post, result);
    }

    @Test
    @DisplayName("Get post with invalid id")
    void testGetPostByIdInvalidId() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> postService.getPostById(1L));
        assertEquals("Post with id: 1 not found", ex.getMessage());
    }

    private Post createTestPost() {
        return Post.builder()
                .id(1L)
                .content("Test content")
                .build();
    }
}