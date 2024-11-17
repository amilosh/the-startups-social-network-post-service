package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    long commentId;
    Comment comment;

    @BeforeEach
    public void setUp(){
        commentId = 5L;

        comment = Comment.builder()
                .id(commentId)
                .build();
    }

    @Test
    public void testGetPostByIdWithExistentPost(){
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.ofNullable(comment));

        Comment result = commentService.getCommentById(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
    }

    @Test
    public void testGetPostByIdWhenPostNotExist(){
        when(commentRepository.findById(commentId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getCommentById(commentId));
    }

    @Test
    public void testIsPostNotExistWithExistentPost(){
        when(commentRepository.existsById(commentId)).thenReturn(true);

        boolean result = commentService.isCommentNotExist(commentId);

        assertFalse(result);
    }

    @Test
    public void testIsPostNotExistWhenPostNotExist() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        boolean result = commentService.isCommentNotExist(commentId);

        assertTrue(result);
    }
}