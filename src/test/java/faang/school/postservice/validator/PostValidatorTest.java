package faang.school.postservice.validator;

import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    Long postId = 1L;

    @Test
    @DisplayName("Check post exists by id success")
    void testValidatePostExistsById() {
        when(postRepository.existsById(postId)).thenReturn(true);

        postValidator.validatePostExistsById(postId);

        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    @DisplayName("Check post exists by id fail")
    void testValidatePostExistsByIdFail() {
        when(postRepository.existsById(postId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> postValidator.validatePostExistsById(postId));
        assertEquals("Post with id #1 doesn't exist", ex.getMessage());
    }
}