package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostService postService;

    @Mock
    CommentMapper commentMapper;

    @Mock
    PostValidator postValidator;

    @Mock
    CommentValidator commentValidator;

    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    CommentService commentService;

    CreateCommentDto dto;
    UpdateCommentDto updateDto;
    CommentDto responseDto;
    Post post;
    Comment comment;
    long postId = 1L;

    @BeforeEach
    void setUp() {
        dto = createTestCreateCommentDto();
        updateDto = createTestUpdateCommentDto();
        responseDto = createCommentTestCommentDto();
        post = createCommentTestPost();
        comment = createCommentTestComment();
    }

    @Test
    @DisplayName("Create comment success")
    void testCreateCommentCommentSuccess() {

        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(postService.getPostById(postId)).thenReturn(post);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentDto result = commentService.createComment(postId, dto);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, times(1)).getUser(dto.getAuthorId());
        verify(commentRepository, times(1)).save(any(Comment.class));

        assertNotNull(result);
        assertEquals(responseDto, result);
        assertEquals(1L, result.getPostId());
    }

    @Test
    @DisplayName("Create comment fail: invalid post id")
    void testCreateCommentCommentFailInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postValidator).validatePostExistsById(postId);


        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(postId, dto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, never()).getUser(dto.getAuthorId());
        verify(commentMapper, never()).toEntity(dto);
        verify(postService, never()).getPostById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Create comment fail: invalid author id")
    void testCreateCommentCommentFailInvalidAuthorId() {
        doThrow(EntityNotFoundException.class).when(userServiceClient).getUser(dto.getAuthorId());

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(postId, dto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, times(1)).getUser(dto.getAuthorId());
        verify(commentMapper, never()).toEntity(dto);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Update comment success")
    void testUpdateCommentSuccess() {
        comment.setPost(post);
        when(commentRepository.findById(updateDto.getId())).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentDto result = commentService.updateComment(postId, updateDto);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, times(1)).validateCommentExistsById(updateDto.getId());
        verify(userServiceClient, times(1)).getUser(updateDto.getAuthorId());
        verify(commentValidator, times(1)).validateCommentAuthorId(comment, updateDto.getAuthorId());
        verify(commentRepository, times(1)).save(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
    }

    @Test
    @DisplayName("Update comment fail: invalid post id")
    void testUpdateCommentFailInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postValidator).validatePostExistsById(postId);

        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(postId, updateDto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, never()).validateCommentExistsById(updateDto.getId());
        verify(userServiceClient, never()).getUser(updateDto.getAuthorId());
        verify(commentValidator, never()).validateCommentAuthorId(comment, updateDto.getAuthorId());
        verify(commentRepository, never()).save(comment);
    }

    @Test
    @DisplayName("Update comment fail: invalid comment id")
    void testUpdateCommentFailInvalidCommentId() {
        when(commentRepository.findById(updateDto.getId())).thenReturn(Optional.empty());

        Exception ex =assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(postId, updateDto));
        assertEquals("Comment with id #3 doesn't exist", ex.getMessage());

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, times(1)).validateCommentExistsById(updateDto.getId());
        verify(userServiceClient, times(1)).getUser(updateDto.getAuthorId());
        verify(commentValidator, never()).validateCommentAuthorId(comment, updateDto.getAuthorId());
        verify(commentRepository, never()).save(comment);
    }

    private CreateCommentDto createTestCreateCommentDto() {
        return CreateCommentDto.builder()
                .authorId(1L)
                .content("Test content")
                .build();
    }

    private CommentDto createCommentTestCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likeIds(null)
                .postId(1L)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    private UpdateCommentDto createTestUpdateCommentDto() {
        return UpdateCommentDto.builder()
                .id(3L)
                .authorId(1L)
                .content("Test update content")
                .build();
    }

    private Post createCommentTestPost() {
        return Post.builder()
                .id(1L)
                .content("Test content")
                .comments(null)
                .build();
    }

    private Comment createCommentTestComment() {
        return Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likes(null)
                .post(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}