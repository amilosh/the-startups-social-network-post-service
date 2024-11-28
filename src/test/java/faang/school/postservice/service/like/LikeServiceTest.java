package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.mapper.like.LikeMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Captor
    private ArgumentCaptor<Like> likeCaptor;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private LikeMapperImpl likeMapper;
    @Mock
    private LikeValidator validator;
    @Mock
    private UserServiceClient userServiceClient;


    private static final int BATCH_SIZE = 100;

    private LikeRequestDto acceptanceLikeDto;
    private Post post;
    private Like like;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        acceptanceLikeDto = LikeRequestDto.builder()
                .userId(1L)
                .build();
        comment = new Comment();
        comment.setId(10L);
        comment.setLikes(new ArrayList<>());
        post = new Post();
        post.setId(5L);
        post.setLikes(new ArrayList<>());
        like = new Like();
        like.setId(1L);
        like.setUserId(1L);

    }

    @Test
    public void testPostLikeSuccess() {
        when(postRepository.findById(5L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHasLike(5L, 1L)).thenReturn(true);

        likeService.postLike(acceptanceLikeDto, 5L);

        verify(likeRepository).save(likeCaptor.capture());
        verify(postRepository).save(post);
        Like like = likeCaptor.getValue();

        assertEquals(like.getPost(), post);
        assertEquals(post.getLikes(), List.of(like));
    }

    @Test
    public void testPostLikeWithPostHatLike() {
        when(postRepository.findById(5L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHasLike(5L, 1L)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> likeService.postLike(acceptanceLikeDto, 5L));
    }

    @Test
    public void testCommentLikeSuccess() {
        when(commentRepository.findById(10L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHasLike(10L, 1L)).thenReturn(true);

        likeService.commentLike(acceptanceLikeDto, 10L);

        verify(likeRepository).save(likeCaptor.capture());
        verify(commentRepository).save(comment);
        Like like = likeCaptor.getValue();

        assertEquals(like.getComment(), comment);
        assertEquals(comment.getLikes(), List.of(like));
    }

    @Test
    public void testCommentLikeWithCommentHatLike() {
        when(commentRepository.findById(10L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHasLike(10L, 1L)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> likeService.commentLike(acceptanceLikeDto, 10L));
    }

    @Test
    public void testDeleteLikeFromPost() {
        post.getLikes().add(like);
        when(postRepository.findById(5L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHasLike(5L, 1L)).thenReturn(false);
        likeService.deleteLikeFromPost(acceptanceLikeDto, 5L);
        verify(likeRepository).deleteByPostIdAndUserId(post.getId(), 1L);

        assertTrue(post.getLikes().isEmpty());
    }

    @Test
    public void testDeleteLikeFromPostWithPostHasNotLike() {
        when(postRepository.findById(5L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHasLike(5L, 1L)).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> likeService.deleteLikeFromPost(acceptanceLikeDto, 5L));
    }

    @Test
    public void testDeleteLikeFromComment() {
        comment.getLikes().add(like);
        when(commentRepository.findById(10L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHasLike(10L, 1L)).thenReturn(false);
        likeService.deleteLikeFromComment(acceptanceLikeDto, 10L);
        verify(likeRepository).deleteByCommentIdAndUserId(comment.getId(), 1L);
        assertTrue(comment.getLikes().isEmpty());
    }

    @Test
    public void testDeleteLikeFromCommentWithCommentHasNotLike() {
        when(commentRepository.findById(10L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHasLike(10L, 1L)).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> likeService.deleteLikeFromComment(acceptanceLikeDto, 10L));
    }

    @Test
    void testGetUsersByPostId_Success() {
        List<Like> likes = List.of(new Like(1L, 1L), new Like(2L, 2L));
        List<Long> userIds = List.of(1L, 2L);
        List<UserDto> userDtos = List.of(
                new UserDto(1L, "User1", "user1@example.com", "Address1", 25),
                new UserDto(2L, "User2", "user2@example.com", "Address2", 30)
        );

        when(likeRepository.findByPostId(5L)).thenReturn(likes);
        when(validator.validatePostHasLikes(5L, userIds)).thenReturn(true);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getUsersByPostId(5L);

        assertEquals(2, result.size());
        assertTrue(result.contains(new UserDto(1L, "User1", "user1@example.com", "Address1", 25)));
        assertTrue(result.contains(new UserDto(2L, "User2", "user2@example.com", "Address2", 30)));

        verify(likeRepository).findByPostId(5L);
        verify(userServiceClient).getUsersByIds(userIds);
    }

    @Test
    void testGetUsersByPostId_NoLikes() {
        when(likeRepository.findByPostId(5L)).thenReturn(List.of());

        List<UserDto> result = likeService.getUsersByPostId(5L);

        assertEquals(1, result.size());
        assertEquals("Some users have not liked the given post.", result.get(0).getEmail());

        verify(likeRepository).findByPostId(5L);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void testGetUsersByPostId_UserServiceError() {
        List<Like> likes = List.of(new Like(1L, 1L), new Like(2L, 2L));
        List<Long> userIds = List.of(1L, 2L);

        when(likeRepository.findByPostId(5L)).thenReturn(likes);
        when(validator.validatePostHasLikes(5L, userIds)).thenReturn(true);
        when(userServiceClient.getUsersByIds(anyList())).thenThrow(new RuntimeException("Service unavailable"));

        List<UserDto> result = likeService.getUsersByPostId(5L);

        assertTrue(result.isEmpty());
        verify(likeRepository).findByPostId(5L);
        verify(userServiceClient).getUsersByIds(userIds);
    }
}
