package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Spy
    private CommentMapper commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void createCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        when(postService.getPostById(commentDto.getPostId())).thenReturn(new PostDto());
        doNothing().when(commentValidator).isAuthorExist(commentDto.getAuthorId());
        Comment comment = new Comment();
        doReturn(comment).when(commentMapper).toEntity(commentDto);

        CommentDto result = commentService.createComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getContent(), result.getContent());

        verify(postService, times(1)).getPostById(commentDto.getPostId());
        verify(commentValidator, times(1)).isAuthorExist(commentDto.getAuthorId());
        verify(commentMapper, times(1)).toEntity(commentDto);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void updateComment() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Test");

        Comment comment = new Comment();
        comment.setContent("Old content");

        when(commentValidator.getExistingComment(commentId)).thenReturn(comment);

        CommentDto mappedCommentDto = new CommentDto();
        mappedCommentDto.setContent(commentDto.getContent());
        when(commentMapper.toDto(comment)).thenReturn(mappedCommentDto);

        CommentDto result = commentService.updateComment(commentId, commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getContent(), result.getContent());

        verify(commentValidator, times(1)).getExistingComment(commentId);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toDto(comment);
    }

    @Test
    public void getAllComments() {
        Long postId = 1L;
        when(postService.getPostById(postId)).thenReturn(new PostDto());

        Comment comment1 = new Comment();
        comment1.setCreatedAt(LocalDateTime.of(2024, 11, 11, 12, 0));
        Comment comment2 = new Comment();
        comment2.setCreatedAt(LocalDateTime.of(2024, 10, 11, 12, 0));
        List<Comment> comments = List.of(comment1, comment2);
        when(commentRepository.findAllByPostId(1L)).thenReturn(comments);
        commentService.getAllComments(postId);

        verify(postService, times(1)).getPostById(postId);
        verify(commentRepository, times(1)).findAllByPostId(postId);
        verify(commentMapper, times(comments.size())).toDto(any());
    }

    @Test
    public void deleteCommentTest() {
        Long authorId = 2L;
        Long commentId = 2L;
        Comment comment = new Comment();
        comment.setAuthorId(commentId);

        doNothing().when(commentValidator).isAuthorExist(authorId);
        when(commentValidator.getExistingComment(commentId)).thenReturn(comment);

        commentService.deleteComment(authorId, commentId);

        verify(commentValidator, times(1)).isAuthorExist(authorId);
        verify(commentValidator, times(1)).getExistingComment(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

}
