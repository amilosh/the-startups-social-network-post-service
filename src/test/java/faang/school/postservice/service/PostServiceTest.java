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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    public void testGetById() {
        // arrange
        long postId = 5L;
        Post post = Post.builder()
                .id(postId)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // act
        Post returnedPost = postService.getById(postId);

        // assert
        assertEquals(post, returnedPost);
    }

    @Test
    public void testGetByIdPostNotFound() {
        // arrange
        long postId = 5L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> postService.getById(postId));
    }
}
