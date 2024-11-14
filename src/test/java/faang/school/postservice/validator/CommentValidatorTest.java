package faang.school.postservice.validator;

import faang.school.postservice.exception.UnauthorizedAccessException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentValidator commentValidator;

    Comment comment;

    @BeforeEach
    void setUp() {
        comment = createTestComment();
    }

    @Test
    @DisplayName("Validate comment exists by id success")
    void testValidateCommentExistsByIdSuccess() {
        when(commentRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> commentValidator.validateCommentExistsById(1L));
    }

    @Test
    @DisplayName("Validate comment exists by id fail: invalid id")
    void testValidateCommentExistsByIdFailInvalidId() {
        when(commentRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentValidator.validateCommentExistsById(1L));
    }

    @Test
    @DisplayName("Validate author of the comment success")
    void testValidateCommentAuthorIdSuccess() {
        long authorId = 1L;

        assertDoesNotThrow(() -> commentValidator.validateCommentAuthorId(comment, authorId));
    }

    @Test
    @DisplayName("Validate author of the comment fail: ids don't match")
    void testValidateCommentAuthorIdFail() {
        long authorId = 2L;

        assertThrows(UnauthorizedAccessException.class, () -> commentValidator.validateCommentAuthorId(comment, authorId));
    }

    private Comment createTestComment() {
        return Comment.builder()
                .authorId(1L)
                .build();
    }


}