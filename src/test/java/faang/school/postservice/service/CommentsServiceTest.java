package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_Success() {
        Long postId = 100L;
        Comment comment = creatTestComment();
        CommentDto commentDto = creatTestCommentDto();


        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.createComment(postId,commentDto);

        verify(commentRepository).save(any(Comment.class));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test Comment");
        assertThat(result.getAuthorId()).isEqualTo(2L);
        assertThat(result.getPostId()).isEqualTo(postId);
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void updateComment_Success() {

    }

    @Test
    void getAllComments_Success() {

    }

    @Test
    void deleteComment_Success() {

    }

    private CommentDto creatTestCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)
                .postId(100L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Comment creatTestComment() {
        return Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)

                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
