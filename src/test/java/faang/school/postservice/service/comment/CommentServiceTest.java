package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.RequestCommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private static final Long VALID_COMMENT_ID = 1L;
    private static final Long VALID_POST_ID = 22L;
    private static final String VALID_CONTENT = "some content";
    private static final String UPDATED_CONTENT = "some other content";
    private static final LocalDateTime CREATED_AT_FOR_OLDER_COMMENT =
            LocalDateTime.of(2023, 11, 10, 10, 0);
    private static final LocalDateTime CREATED_AT_FOR_NEWER_COMMENT =
            LocalDateTime.of(2024, 11, 11, 10, 0);

    @Test
    void createComment_shouldCreateCommentSuccessfully() {
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        requestCommentDto.setId(null);

        Comment comment = new Comment();
        ResponseCommentDto expectedOutput = new ResponseCommentDto();

        when(commentMapper.toEntity(requestCommentDto)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(expectedOutput);

        ResponseCommentDto actualOutput = commentService.createComment(requestCommentDto);

        verify(commentValidator).validateAuthorExists(requestCommentDto);
        verify(commentValidator).validatePostExists(requestCommentDto.getPostId());
        verify(commentRepository).save(comment);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void updateComment_shouldUpdateCommentSuccessfully() {
        RequestCommentUpdateDto requestCommentUpdateDto = new RequestCommentUpdateDto();
        requestCommentUpdateDto.setCommentId(VALID_COMMENT_ID);
        requestCommentUpdateDto.setContent(UPDATED_CONTENT);

        Comment existingComment = new Comment();
        existingComment.setId(VALID_COMMENT_ID);
        existingComment.setContent(VALID_CONTENT);

        ResponseCommentDto expectedOutput = new ResponseCommentDto();

        when(commentRepository.getCommentById(requestCommentUpdateDto.getCommentId())).thenReturn(existingComment);
        when(commentMapper.toDto(existingComment)).thenReturn(expectedOutput);

        ResponseCommentDto actualOutput = commentService.updateComment(requestCommentUpdateDto);

        verify(commentValidator).validateCommentExists(requestCommentUpdateDto.getCommentId());
        verify(commentRepository).save(existingComment);
        assertEquals(UPDATED_CONTENT, existingComment.getContent());
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getCommentsByPostId_shouldReturnCommentsInDescendingOrder() {
        Long postId = VALID_POST_ID;

        Comment comment1 = new Comment();
        comment1.setCreatedAt(CREATED_AT_FOR_OLDER_COMMENT);

        Comment comment2 = new Comment();
        comment2.setCreatedAt(CREATED_AT_FOR_NEWER_COMMENT);

        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);

        ResponseCommentDto dto1 = new ResponseCommentDto();
        ResponseCommentDto dto2 = new ResponseCommentDto();

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDto(comments)).thenReturn(List.of(dto2, dto1)); // In reversed order

        List<ResponseCommentDto> actualOutput = commentService.getCommentsByPostId(postId);

        verify(commentValidator).validatePostExists(postId);
        comments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        assertEquals(List.of(dto2, dto1), actualOutput);
    }

    @Test
    void deleteComment_shouldDeleteCommentSuccessfully() {
        Long commentId = VALID_COMMENT_ID;

        commentService.deleteComment(commentId);

        verify(commentRepository).deleteById(commentId);
        verifyNoMoreInteractions(commentRepository);
    }
}