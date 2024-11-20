package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeCommentMapperImpl;
import faang.school.postservice.mapper.like.LikePostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikePostMapperImpl likePostMapper;
    @Mock
    private LikeCommentMapperImpl likeCommentMapper;
    @Mock
    private UserServiceClient userServiceClient;


    @Test
    void likePost_shouldCreateLikeForPost_whenDataIsValid() {
        LikePostDto likePostDto = getLikePostDto();
        LikePostDto expectedLikePostDto = getExpectedPostDto();
        Post post = getPost();
        Like like = getLike();

        when(postRepository.findById(likePostDto.postId())).thenReturn(Optional.of(post));
        when(likePostMapper.toEntity(any())).thenReturn(like);
        when(likePostMapper.toDto(any())).thenReturn(expectedLikePostDto);
        when(likeRepository.save(like)).thenReturn(like);

        LikePostDto actualLikePost = likeService.likePost(likePostDto);

        verify(likeRepository).save(like);

        assertNotNull(actualLikePost);
        assertThat(actualLikePost.id()).isEqualTo(expectedLikePostDto.id());


    }

    @Test
    void likePost_shouldThrowIllegalArgumentException_whenPostHasAlreadyLikeBySameUser() {
        LikePostDto likePostDto = getLikePostDto();
        LikePostDto expectedLikePostDto = getExpectedPostDto();
        Post post = getPost();
        post.setLikes(List.of(new Like(1L, 1L, null, null, null)));
        Like like = getLike();

        when(postRepository.findById(likePostDto.postId())).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(likePostDto.postId(), likePostDto.userId()))
                .thenReturn(Optional.of(new Like(1L, 1L, null, null, null)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.likePost(expectedLikePostDto));

        verify(likeRepository, never()).save(like);

        assertThat(exception.getMessage()).isEqualTo("This post was already liked by this user");
    }

    @Test
    void likePost_shouldThrowEntityNotFoundException_whenPostNotFound() {
        LikePostDto likePostDto = getLikePostDto();

        when(postRepository.findById(any())).thenThrow(EntityNotFoundException.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.likePost(likePostDto));

        verify(likeRepository, never()).save(any());

        assertThat(exception.getClass()).isEqualTo(EntityNotFoundException.class);
    }

    @Test
    void unlikePost_shouldUnlikePost() {
        Like like = getLike();
        long postId = 1L;
        long userId = 1L;
        UserDto mockUser = new UserDto(userId, "testuser", "test@gmail.com");

        when(userServiceClient.getUser(userId)).thenReturn(mockUser);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        likeService.unlikePost(postId, userId);

        verify(userServiceClient, times(1)).getUser(userId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void unlikePost_shouldThrowEntityNotFoundException_whenLikeOfPostNotFound() {
        long postId = 1L;
        long userId = 1L;
        UserDto mockUser = new UserDto(userId, "testuser", "test@gmail.com");

        when(userServiceClient.getUser(userId)).thenReturn(mockUser);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.unlikePost(postId, userId));

        assertThat(exception.getMessage()).isEqualTo("Like not found for postId: " + postId + " and userId: " + userId);
    }

    @Test
    void likeComment_shouldLikeComment_whenDataIsValid() {
        Post post = getPost();
        Comment comment = getComment(post);
        Like like = getLike();
        LikeCommentDto likeCommentDto = getLikeCommentDto();
        LikeCommentDto expectedLikeCommentDto = getExpectedCommentDto();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeCommentMapper.toEntity(any())).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);

        LikeCommentDto actualLikeComment = likeService.likeComment(likeCommentDto);

        verify(likeRepository, times(1)).save(like);

        assertNotNull(actualLikeComment);
        assertThat(actualLikeComment.id()).isEqualTo(expectedLikeCommentDto.id());
    }

    @Test
    void likeComment_shouldThrowIllegalArgumentException_whenCommentHasAlreadyLikeBySameUser() {
        LikeCommentDto likeCommentDto = getLikeCommentDto();
        Post post = getPost();
        Comment comment = getComment(post);
        comment.setLikes(List.of(new Like(1L, 1L, null, null, null)));
        Like like = getLike();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(likeCommentDto.postId(), likeCommentDto.userId()))
                .thenReturn(Optional.of(new Like(1L, 1L, null, null, null)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                likeService.likeComment(likeCommentDto));

        verify(likeRepository, never()).save(like);

        assertThat(exception.getMessage()).isEqualTo("This comment was already liked by this user");
    }

    @Test
    void likeComment_shouldThrowEntityNotFoundException_whenPostNotFound() {
        LikeCommentDto likeCommentDto = getExpectedCommentDto();


        when(commentRepository.findById(any())).thenThrow(EntityNotFoundException.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.likeComment(likeCommentDto));

        verify(likeRepository, never()).save(any());

        assertThat(exception.getClass()).isEqualTo(EntityNotFoundException.class);
    }

    @Test
    void unlikeComment_shouldUnlikeComment() {
        Like like = getLike();
        long commentId = 1L;
        long userId = 1L;
        UserDto mockUser = new UserDto(userId, "testuser", "test@gmail.com");

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(like));
        when(userServiceClient.getUser(userId)).thenReturn(mockUser);

        likeService.unlikeComment(commentId, userId);

        verify(userServiceClient, times(1)).getUser(userId);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void unlikeComment_shouldThrowEntityNotFoundException_whenLikeOfCommentNotFound() {
        long commentId = 1L;
        long userId = 1L;
        UserDto mockUser = new UserDto(userId, "testuser", "test@gmail.com");

        when(userServiceClient.getUser(userId)).thenReturn(mockUser);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.unlikeComment(commentId, userId));

        assertThat(exception.getMessage()).isEqualTo("Like not found for commentId: " + commentId + " and userId: " + userId);
    }

    private Post getPost(){
        return Post.builder()
                .id(1L)
                .projectId(1L)
                .likes(List.of(new Like(), new Like(), new Like()))
                .comments(List.of(new Comment(), new Comment()))
                .build();
    }

    private Comment getComment(Post post){
        return Comment.builder()
                .id(1L)
                .likes(List.of(new Like(), new Like()))
                .post(post)
                .build();
    }

    private LikePostDto getExpectedPostDto(){
        return LikePostDto.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .build();
    }

    private LikeCommentDto getExpectedCommentDto(){
        return LikeCommentDto.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .commentId(1L)
                .build();
    }

    private LikePostDto getLikePostDto(){
        return LikePostDto.builder()
                .id(null)
                .userId(1L)
                .postId(1L)
                .build();
    }

    private LikeCommentDto getLikeCommentDto(){
        return LikeCommentDto.builder()
                .id(null)
                .userId(1L)
                .postId(1L)
                .commentId(1L)
                .build();
    }

    private Like getLike(){
        return Like.builder()
                .id(1L)
                .userId(1L)
                .build();
    }
}