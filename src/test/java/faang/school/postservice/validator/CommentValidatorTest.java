package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.RequestCommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentValidator commentValidator;

    private static final String EMPTY_CONTENT = "";
    private static final String LONGER_THAN_4096_SYMBOLS_CONTENT = "a".repeat(4097);
    private static final String VALID_CONTENT = "some content";
    private static final Long VALID_COMMENT_ID = 1L;
    private static final Long VALID_AUTHOR_ID = 1L;
    private static final Long VALID_POST_ID = 1L;
    private static final UserDto VALID_USER_DTO =
            new UserDto(1L, "John Doe", "JohnDoe@gmail.com");

    @Test
    public void validateComment_shouldThrowException_whenContentIsNull() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setContent(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> commentValidator.validateCommentDtoInput(requestCommentDto));

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenContentIsEmpty() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setContent(EMPTY_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentDtoInput(requestCommentDto);
        });

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenContentIsTooLong() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setContent(LONGER_THAN_4096_SYMBOLS_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentDtoInput(requestCommentDto);
        });

        assertEquals("Comment content is too long", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenAuthorIdIsNull() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setContent(VALID_CONTENT);
        requestCommentDto.setAuthorId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentDtoInput(requestCommentDto);
        });

        assertEquals("Comment must have an author", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenPostIdIsNull() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setAuthorId(VALID_AUTHOR_ID);
        requestCommentDto.setContent(VALID_CONTENT);
        requestCommentDto.setPostId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentDtoInput(requestCommentDto);
        });

        assertEquals("Comment must relate to a post", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenContentIsNull() {
        RequestCommentUpdateDto updatingDto = new RequestCommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenContentIsEmpty() {
        RequestCommentUpdateDto updatingDto = new RequestCommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(EMPTY_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenContentIsTooLong() {
        RequestCommentUpdateDto updatingDto = new RequestCommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(LONGER_THAN_4096_SYMBOLS_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment content is too long", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldPass_whenAllValidationsPass() {
        RequestCommentUpdateDto updatingDto = new RequestCommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(VALID_CONTENT);

        assertDoesNotThrow(() -> commentValidator.validateCommentUpdateDto(updatingDto));
    }

    @Test
    void validateCommentExists_shouldThrowException_whenCommentDoesNotExist() {
        Long commentId = VALID_COMMENT_ID;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentValidator.validateCommentExists(commentId);
        });

        assertEquals("Comment with id " + commentId + " does not exist", exception.getMessage());
    }

    @Test
    public void validatePostExists_shouldThrowException_whenPostDoesNotExist() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setPostId(2L);

        when(postRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentValidator.validatePostExists(requestCommentDto.getPostId());
        });

        assertEquals("Post with id 2 does not exist", exception.getMessage());
    }

    @Test
    public void validatePostExists_shouldPass_whenPostExists() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setPostId(VALID_POST_ID);

        when(postRepository.findById(requestCommentDto.getPostId())).thenReturn(java.util.Optional.of(new Post()));

        assertDoesNotThrow(() -> commentValidator.validatePostExists(requestCommentDto.getPostId()));
    }

    @Test
    public void validateAuthorExists_shouldThrowException_whenUserDoesNotExist() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setAuthorId(VALID_AUTHOR_ID);

        when(userServiceClient.getUser(VALID_AUTHOR_ID))
                .thenThrow(new IllegalArgumentException("User with id 1 does not exist"));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateAuthorExists(requestCommentDto);
        });

        assertEquals("User with id 1 does not exist", exception.getMessage());
    }

    @Test
    public void validateAuthorExists_shouldPass_whenUserExists() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setAuthorId(VALID_AUTHOR_ID);

        when(userServiceClient.getUser(1L)).thenReturn(VALID_USER_DTO);

        assertDoesNotThrow(() -> commentValidator.validateAuthorExists(requestCommentDto));
    }

    @Test
    public void validateCommentIdNotNullForCreatingNewComment_shouldThrowException_whenIdIsNotNull() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setId(VALID_COMMENT_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentIdIsNullForCreatingNewComment(requestCommentDto);
        });

        assertEquals("Comment id must be null to create a new comment", exception.getMessage());
    }

    @Test
    public void validateCommentIdIsNullForCreatingNewComment_shouldPass_whenIdIsNull() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setId(null);

        assertDoesNotThrow(() -> commentValidator.validateCommentIdIsNullForCreatingNewComment(requestCommentDto));
    }
}