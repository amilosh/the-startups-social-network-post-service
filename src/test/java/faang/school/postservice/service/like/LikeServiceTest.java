package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.mapper.like.LikeCommentMapperImpl;
import faang.school.postservice.mapper.like.LikePostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostServiceImpl;
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
    private PostServiceImpl postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikePostMapperImpl likePostMapper;
    @Mock
    private LikeCommentMapperImpl likeCommentMapper;


    @Test
    void likePost_shouldCreateLikeForPost_whenDataIsValid() {
        LikePostDto likePostDto = getLikePostDto();
        LikePostDto expectedLikePostDto = getExpectedPostDto();
        Post post = getPost();
        Like like = getLike();

        when(postService.findPost(likePostDto.postId())).thenReturn(post);
        when(likePostMapper.toEntity(any())).thenReturn(like);
        when(likePostMapper.toDto(any())).thenReturn(expectedLikePostDto);
        when(likeRepository.save(like)).thenReturn(like);

        LikePostDto actualLikePost = likeService.likePost(likePostDto);

        verify(likeRepository).save(like);

        assertNotNull(actualLikePost);
        assertThat(actualLikePost.id()).isEqualTo(expectedLikePostDto.id());
        assertThat(actualLikePost.numberOfLikes()).isEqualTo(expectedLikePostDto.numberOfLikes());


    }

    @Test
    void likePost_shouldThrowIllegalArgumentException_whenPostHasAlreadyLikeBySameUser() {
        LikePostDto likePostDto = getLikePostDto();
        LikePostDto expectedLikePostDto = getExpectedPostDto();
        Post post = getPost();
        post.setLikes(List.of(new Like(1L, 1L, null, null, null)));
        Like like = getLike();

        when(postService.findPost(likePostDto.postId())).thenReturn(post);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.likePost(expectedLikePostDto));

        verify(likeRepository, never()).save(like);

        assertThat(exception.getMessage()).isEqualTo("This post was already liked by this user");
    }

    @Test
    void likePost_shouldThrowEntityNotFoundException_whenPostNotFound() {
        LikePostDto likePostDto = getLikePostDto();

        when(postService.findPost(any())).thenThrow(EntityNotFoundException.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.likePost(likePostDto));

        verify(likeRepository, never()).save(any());

        assertThat(exception.getClass()).isEqualTo(EntityNotFoundException.class);
    }

    @Test
    void unlikePost_shouldUnlikePost() {
        Like like = getLike();
        when(likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        likeService.unlikePost(like.getId());

        verify(likeRepository, times(1)).deleteById(like.getId());
    }

    @Test
    void unlikePost_shouldEntityNotFoundException_whenLikeNotFound() {
        Like like = getLike();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.unlikePost(like.getId()));

        verify(likeRepository, never()).deleteById(like.getId());

        assertThat(exception.getClass()).isEqualTo(EntityNotFoundException.class);
    }

    @Test
    void likeComment_shouldLikeComment_whenDataIsValid() {
        Post post = getPost();
        Comment comment = getComment(post);
        Like like = getLike();
        LikeCommentDto likeCommentDto = getLikeCommentDto();
        LikeCommentDto expectedLikeCommentDto = getExpectedCommentDto();

        when(postService.findPost(post.getId())).thenReturn(post);
        when(commentService.findComment(comment.getId())).thenReturn(comment);
        when(likeCommentMapper.toEntity(any())).thenReturn(like);
        when(likeCommentMapper.toDto(any(), any())).thenReturn(expectedLikeCommentDto);
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

        when(postService.findPost(likeCommentDto.postId())).thenReturn(post);
        when(commentService.findComment(likeCommentDto.commentId())).thenReturn(comment);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                likeService.likeComment(likeCommentDto));

        verify(likeRepository, never()).save(like);

        assertThat(exception.getMessage()).isEqualTo("This comment was already liked by this user");
    }

    @Test
    void likeComment_shouldThrowEntityNotFoundException_whenPostNotFound() {
        Post post = getPost();
        LikeCommentDto likeCommentDto = getExpectedCommentDto();

        when(postService.findPost(any())).thenReturn(post);
        when(commentService.findComment(any())).thenThrow(EntityNotFoundException.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.likeComment(likeCommentDto));

        verify(likeRepository, never()).save(any());

        assertThat(exception.getClass()).isEqualTo(EntityNotFoundException.class);
    }

    @Test
    void unlikeComment_shouldUnlikeComment() {
        Like like = getLike();
        when(likeRepository.findById(like.getId())).thenReturn(Optional.of(like));

        likeService.unlikeComment(like.getId());

        verify(likeRepository, times(1)).deleteById(like.getId());
    }

    @Test
    void unlikeComment_shouldEntityNotFoundException_whenLikeNotFound() {
        Like like = getLike();
        when(likeRepository.findById(like.getId())).thenThrow(EntityNotFoundException.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.unlikeComment(like.getId()));

        verify(likeRepository, never()).deleteById(like.getId());

        assertThat(exception.getClass()).isEqualTo(EntityNotFoundException.class);
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
                .numberOfLikes(3L)
                .build();
    }

    private LikeCommentDto getExpectedCommentDto(){
        return LikeCommentDto.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .commentId(1L)
                .numberOfLikes(3L)
                .build();
    }

    private LikePostDto getLikePostDto(){
        return LikePostDto.builder()
                .id(null)
                .userId(1L)
                .postId(1L)
                .numberOfLikes(null)
                .build();
    }

    private LikeCommentDto getLikeCommentDto(){
        return LikeCommentDto.builder()
                .id(null)
                .userId(1L)
                .postId(1L)
                .commentId(1L)
                .numberOfLikes(null)
                .build();
    }

    private Like getLike(){
        return Like.builder()
                .id(1L)
                .userId(1L)
                .build();
    }
}