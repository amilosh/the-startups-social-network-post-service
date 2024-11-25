package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;
    @InjectMocks
    PostService postService;

    @Test
    void testFindEntityById() {
        Post post = new Post();
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.of(post));
        Post returnedPost = assertDoesNotThrow(() -> postService.findEntityById(1));
        assertEquals(post, returnedPost);
    }

    @Test
    void testFindEntityByIdFail() {
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> postService.findEntityById(1));
    }

}
