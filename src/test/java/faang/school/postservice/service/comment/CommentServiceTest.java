package faang.school.postservice.service.comment;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void getCommentThrowExceptionTest() {
        long id = 1L;
        when(commentRepository.findById(id)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getEntityComment(id));
    }

    @Test
    public void getEntityCommentTest() {
        long id = 1L;
        Comment comment = new Comment();
        comment.setId(id);
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Comment resultComment = commentService.getEntityComment(id);

        assertEquals(id, resultComment.getId());
        assertEquals(comment, resultComment);
    }

    @Test
    public void addLikeToCommentTest() {
        long id = 1L;
        Comment comment = Comment.builder()
                .id(id).build();
        Like like = Like.builder()
                .id(id).build();
        List<Like> likes = new ArrayList<>();
        comment.setLikes(likes);

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.addLikeToComment(comment.getId(), like);

        verify(commentRepository).save(comment);
        assertTrue(comment.getLikes().contains(like));
    }

    @Test
    public void removeLikeFromCommentTest() {
        long id = 1L;
        Comment comment = Comment.builder()
                .id(id).build();
        Like like = Like.builder()
                .id(id).build();
        List<Like> likes = new ArrayList<>(List.of(like));
        comment.setLikes(likes);

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.removeLikeFromComment(comment.getId(), like);

        verify(commentRepository).save(comment);
        assertFalse(comment.getLikes().contains(like));
    }
}