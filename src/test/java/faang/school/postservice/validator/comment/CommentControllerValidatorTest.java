package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.CommentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentControllerValidatorTest {

    private CommentControllerValidator validator = new CommentControllerValidator();

    @Test
    void validateCommentDtoSuccess() {
        assertDoesNotThrow(() -> validator.validateCommentDto(new CommentDto()));
    }

    @Test
    void validateCommentDtoFailure() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validateCommentDto(null));
        assertEquals("Request body cannot be null", exception.getMessage());
    }

    @Test
    void validatePostIdSuccess() {
        assertDoesNotThrow(() -> validator.validatePostId(Mockito.anyLong()));
    }

    @Test
    void validatePostIdFailure() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validatePostId(null));
        assertEquals("Post id cannot be null", exception.getMessage());
    }

    @Test
    void validateCommentIdSuccess() {
        assertDoesNotThrow(() -> validator.validateCommentId(Mockito.anyLong()));
    }

    @Test
    void validateCommentIdFailure() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validateCommentId(null));
        assertEquals("Comment id cannot be null", exception.getMessage());
    }
}