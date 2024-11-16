package faang.school.postservice.validator;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    long postId;

    @BeforeEach
    void setUp() {
        postId = 4L;
    }

    @Test
    public void testCheckPostExistenceWherPostExist() {
        when(postRepository.existsById(postId)).thenReturn(true);
        assertDoesNotThrow(() -> postValidator.checkPostExistence(postId),
                "No exception should be thrown if post exists");
        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    public void testCheckPostExistenceWherPostNotFound() {
        when(postRepository.existsById(postId)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postValidator.checkPostExistence(postId);
        });
        verify(postRepository, times(1)).existsById(postId);
    }
}
