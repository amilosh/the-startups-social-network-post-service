package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    UserServiceClient userServiceClient;

    @InjectMocks
    CommentService commentService;

    CreateCommentDto dto;
    CommentDto responseDto;
    Post post;
    Comment comment;
    long postId = 1L;

    @BeforeEach
    void setUp() {
        dto = createTestCreateDto();
        responseDto = createTestCommentDto();
        post = createTestPost();
        comment = createTestComment();
    }

    @Test
    @DisplayName("Create comment success")
    void testCreateCommentSuccess() {

        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(postService.getPostById(postId)).thenReturn(post);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentDto result = commentService.create(postId, dto);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, times(1)).getUser(dto.getAuthorId());
        verify(commentRepository, times(1)).save(any(Comment.class));

        assertNotNull(result);
        assertEquals(responseDto, result);
        assertEquals(1L, result.getPostId());
    }

    @Test
    @DisplayName("Create comment fail: invalid post id")
    void testCreateCommentFailInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postValidator).validatePostExistsById(postId);


        assertThrows(EntityNotFoundException.class, () -> commentService.create(postId, dto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, never()).getUser(dto.getAuthorId());
        verify(commentMapper, never()).toEntity(dto);
        verify(postService, never()).getPostById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Create comment fail: invalid author id")
    void testCreateCommentFailInvalidAuthorId() {
        doThrow(EntityNotFoundException.class).when(userServiceClient).getUser(dto.getAuthorId());

        assertThrows(EntityNotFoundException.class, () -> commentService.create(postId, dto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, times(1)).getUser(dto.getAuthorId());
        verify(commentMapper, never()).toEntity(dto);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    private CreateCommentDto createTestCreateDto() {
        return CreateCommentDto.builder()
                .authorId(1L)
                .content("Test content")
                .build();
    }

    private CommentDto createTestCommentDto() {
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

    private Post createTestPost() {
        return Post.builder()
                .id(1L)
                .content("Test content")
                .comments(null)
                .build();
    }

    private Comment createTestComment() {
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