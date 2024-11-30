package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentValidator commentValidator;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptorFirst;
    private static CommentValidator commentValidatorMock;

    private final Long commentId = 1L;

    @BeforeAll
    public static void setUp() {
        commentValidatorMock = mock(CommentValidator.class);
    }

    @Test
    public void validateContentSuccessTest() {
        String content = "Content";

        doNothing().when(commentValidatorMock).validateContent(stringArgumentCaptor.capture());
        commentValidatorMock.validateContent(content);

        assertEquals(content, stringArgumentCaptor.getValue());
    }

    @Test
    public void validateContentWithContentNullFailTest() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> commentValidator.validateContent(null)
                );

        assertEquals(CommentValidator.CONTENT_IS_EMPTY, illegalArgumentException.getMessage());
    }

    @Test
    public void validateContentWithBlankContentFailTest() {
        String content = " ";

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> commentValidator.validateContent(content)
                );

        assertEquals(CommentValidator.CONTENT_IS_EMPTY, illegalArgumentException.getMessage());
    }

    @Test
    public void validateCommentSuccessTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(2L);
        commentDto.setPostId(3L);

        doNothing().when(commentValidatorMock).validateComment(commentDto);
        commentValidatorMock.validateComment(commentDto);

        verify(commentValidatorMock, times(1)).validateComment(commentDto);

    }

    @Test
    public void validateCommentWithAuthorIdNullFailTest() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> commentValidator.validateComment(new CommentDto())
                );

        assertEquals(CommentValidator.AUTHOR_ID_IS_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void validateCommentWithPostIdNullFailTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(2L);

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> commentValidator.validateComment(commentDto)
                );

        assertEquals(CommentValidator.POST_ID_IS_NULL, illegalArgumentException.getMessage());
    }

    @Test
    public void validateCommentExistWithExistRecommendationSuccessTest() {

        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentValidator.validateCommentExist(commentId);

        verify(commentRepository, times(1)).
                existsById(longArgumentCaptorFirst.capture());
        assertEquals(commentId, longArgumentCaptorFirst.getValue());
    }

    @Test
    public void validateCommentExistWithNotExistCommentFailTest() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class,
                        () -> commentValidator.validateCommentExist(commentId)
                );

        assertEquals(String.format(CommentValidator.COMMENT_NOT_EXIST_BY_ID, commentId), illegalArgumentException.getMessage());
        verify(commentRepository, times(1)).
                existsById(longArgumentCaptorFirst.capture());
        assertEquals(commentId, longArgumentCaptorFirst.getValue());
    }
}
