package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    public void testGetPostNotFound() {
        long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPost(id));
    }

    @Test
    public void testGetPostSuccessful() {
        long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.of(new Post()));
        postService.getPost(id);
    }

    @Test
    public void testExistsPostByIdWhenIdIsNull() {
        assertFalse(postService.existsPostById(null));
    }

    @Test
    public void testExistsPostById() {
        long id = 1L;

        when(postRepository.existsById(id)).thenReturn(true);
        assertTrue(postService.existsPostById(id));
    }
}
