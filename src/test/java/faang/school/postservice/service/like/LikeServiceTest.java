package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.AcceptanceLikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like_validator.LikeValidator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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
    private LikeMapperImpl mapper;
    @Mock
    private LikeValidator validator;

    private AcceptanceLikeDto acceptanceLikeDto;
    private Post post;
    private Like like;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        acceptanceLikeDto = AcceptanceLikeDto.builder()
                .userId(1L)
                .build();
        comment = new Comment();
        comment.setId(1L);
        comment.setLikes(new ArrayList<>());
        post = new Post();
        post.setId(1L);
        post.setLikes(new ArrayList<>());
        like = new Like();
        like.setId(1L);
        like.setUserId(1L);
    }


    @Test
    public void testPostLikeSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHatLike(1L, 1L)).thenReturn(true);

        likeService.postLike(acceptanceLikeDto, 1L);

        verify(likeRepository).save(likeCaptor.capture());
        verify(postRepository).save(post);
        Like like = likeCaptor.getValue();

        assertEquals(like.getPost(), post);
        assertEquals(post.getLikes(), List.of(like));
    }

    @Test
    public void testPostLikeWithPostHatLike() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHatLike(1L, 1L)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> likeService.postLike(acceptanceLikeDto, 1L));
    }

    @Test
    public void testCommentLikeSuccess() {
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHatLike(1L, 1L)).thenReturn(true);

        likeService.commentLike(acceptanceLikeDto, 1L);

        verify(likeRepository).save(likeCaptor.capture());
        verify(commentRepository).save(comment);
        Like like = likeCaptor.getValue();

        assertEquals(like.getComment(), comment);
        assertEquals(comment.getLikes(), List.of(like));
    }

    @Test
    public void testCommentLikeWithCommentHatLike() {
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHatLike(1L, 1L)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> likeService.commentLike(acceptanceLikeDto, 1L));
    }

    @Test
    public void testDeleteLikeFromPost() {
        post.getLikes().add(like);
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHatLike(1L, 1L)).thenReturn(false);
        likeService.deleteLikeFromPost(acceptanceLikeDto, 1L);
        verify(likeRepository).deleteByPostIdAndUserId(post.getId(), 1L);

        assertEquals(new ArrayList<>(), post.getLikes());
    }

    @Test
    public void testDeleteLikeFromPostWithPostHasNotLike() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHatLike(1L, 1L)).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> likeService.deleteLikeFromPost(acceptanceLikeDto, 1L));
    }

    @Test
    public void testDeleteLikeFromComment() {
        comment.getLikes().add(like);
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHatLike(1L, 1L)).thenReturn(false);
        likeService.deleteLikeFromComment(acceptanceLikeDto, 1L);
        verify(likeRepository).deleteByCommentIdAndUserId(post.getId(), 1L);

        assertEquals(new ArrayList<>(), comment.getLikes());
    }

    @Test
    public void testDeleteLikeFromCommentWithCommentHasNotLike() {
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(validator.validateCommentHatLike(1L, 1L)).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> likeService.deleteLikeFromComment(acceptanceLikeDto, 1L));
    }

}
