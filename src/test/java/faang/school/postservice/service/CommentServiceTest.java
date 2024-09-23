package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper mapper;

    private CommentDto commentDto = new CommentDto();
    private CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
    private Long postId = 1L;
    private Long commentId = 1L;
    private Long autrorId = 1L;

    @Test
    void create_nonexistentPost_throwsException() {
        when(postRepository.existsById(postId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> commentService.create(postId, commentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void create_nonexistentUser_throwsException() {
        commentDto.setAuthorId(autrorId);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> commentService.create(postId, commentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void create_validRequest_repositorySaveCalled() {
        Comment comment = new Comment();
        commentDto.setAuthorId(autrorId);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(new UserDto());
        when(mapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        commentService.create(postId, commentDto);
        verify(commentRepository).save(any(Comment.class));
        verify(mapper).toDto(any(Comment.class));
    }

    @Test
    void update_nonexistentComment_throwsException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> commentService.update(commentId, commentUpdateDto));
    }

    @Test
    void update_validUpdate_repositorySaveCalled() {
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.update(commentId, commentUpdateDto);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void getByPostId_validRequest_returnsSortedComments() {
        Comment comment2 = new Comment();
        comment2.setCreatedAt(LocalDateTime
                .of(2024, Month.SEPTEMBER, 23, 15, 30));
        Comment comment1 = new Comment();
        comment1.setCreatedAt(LocalDateTime
                .of(2024, Month.SEPTEMBER, 23, 14, 30));
        Comment comment3 = new Comment();
        comment3.setCreatedAt(LocalDateTime
                .of(2024, Month.SEPTEMBER, 23, 16, 30));
        List<Comment> unsortedComments = new ArrayList<>(
                List.of(comment2, comment3, comment1)
        );
        when(mapper.toDto(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            CommentDto dto = new CommentDto();
            dto.setCreatedAt(comment.getCreatedAt());
            return dto;
        });
        when(commentRepository.findAllByPostId(postId))
                .thenReturn(unsortedComments);
        List<CommentDto> result = commentService.getByPostId(postId);
        assertIterableEquals(List.of(
                mapper.toDto(comment3),
                mapper.toDto(comment2),
                mapper.toDto(comment1)), result);
    }

    @Test
    void delete_validRequest_repositoryCalled() {
        commentRepository.deleteById(commentId);
        verify(commentRepository).deleteById(commentId);
    }
}