package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
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
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setContent(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> commentValidator.validateComment(commentDtoInput));

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenContentIsEmpty() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setContent(EMPTY_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment(commentDtoInput);
        });

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenContentIsTooLong() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setContent(LONGER_THAN_4096_SYMBOLS_CONTENT);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment(commentDtoInput);
        });

        assertEquals("Comment content is too long", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenAuthorIdIsNull() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setContent(VALID_CONTENT);
        commentDtoInput.setAuthorId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment(commentDtoInput);
        });

        assertEquals("Comment must have an author", exception.getMessage());
    }

    @Test
    public void validateComment_shouldThrowException_whenPostIdIsNull() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setAuthorId(VALID_AUTHOR_ID);
        commentDtoInput.setContent(VALID_CONTENT);
        commentDtoInput.setPostId(null);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateComment(commentDtoInput);
        });

        assertEquals("Comment must relate to a post", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenCommentDoesNotExist() {
        CommentUpdateDto updatingDto = new CommentUpdateDto();
        updatingDto.setCommentId(1L); // Non-existing comment ID

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment with id 1 does not exist", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenContentIsNull() {
        CommentUpdateDto updatingDto = new CommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(null);

        when(commentRepository.findById(updatingDto.getCommentId())).thenReturn(Optional.of(new Comment()));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenContentIsEmpty() {
        CommentUpdateDto updatingDto = new CommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(EMPTY_CONTENT);

        when(commentRepository.findById(updatingDto.getCommentId())).thenReturn(Optional.of(new Comment()));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment content is empty", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldThrowException_whenContentIsTooLong() {
        CommentUpdateDto updatingDto = new CommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(LONGER_THAN_4096_SYMBOLS_CONTENT);

        when(commentRepository.findById(updatingDto.getCommentId())).thenReturn(Optional.of(new Comment()));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateCommentUpdateDto(updatingDto);
        });

        assertEquals("Comment content is too long", exception.getMessage());
    }

    @Test
    void validateCommentUpdateDto_shouldPass_whenAllValidationsPass() {
        CommentUpdateDto updatingDto = new CommentUpdateDto();
        updatingDto.setCommentId(VALID_COMMENT_ID);
        updatingDto.setContent(VALID_CONTENT); // Valid content

        when(commentRepository.findById(updatingDto.getCommentId())).thenReturn(Optional.of(new Comment()));

        assertDoesNotThrow(() -> commentValidator.validateCommentUpdateDto(updatingDto));
    }

    @Test
    public void validatePostExists_shouldThrowException_whenPostDoesNotExist() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setPostId(2L);

        when(postRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validatePostExists(commentDtoInput.getPostId());
        });

        assertEquals("Post with id 2 does not exist", exception.getMessage());
    }

    @Test
    public void validatePostExists_shouldPass_whenPostExists() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setPostId(VALID_POST_ID);

        when(postRepository.findById(commentDtoInput.getPostId())).thenReturn(java.util.Optional.of(new Post()));

        assertDoesNotThrow(() -> commentValidator.validatePostExists(commentDtoInput.getPostId()));
    }

    @Test
    public void validateAuthorExists_shouldThrowException_whenUserDoesNotExist() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setAuthorId(VALID_AUTHOR_ID);

        when(userServiceClient.getUser(VALID_AUTHOR_ID))
                .thenThrow(new IllegalArgumentException("User with id 1 does not exist"));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            commentValidator.validateAuthorExists(commentDtoInput);
        });

        assertEquals("User with id 1 does not exist", exception.getMessage());
    }

    @Test
    public void validateAuthorExists_shouldPass_whenUserExists() {
        CommentDtoInput commentDtoInput = new CommentDtoInput();
        commentDtoInput.setAuthorId(VALID_AUTHOR_ID);

        when(userServiceClient.getUser(1L)).thenReturn(VALID_USER_DTO);

        assertDoesNotThrow(() -> commentValidator.validateAuthorExists(commentDtoInput));
    }
}