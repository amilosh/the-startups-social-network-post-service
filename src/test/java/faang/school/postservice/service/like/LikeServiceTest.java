package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.like.LikeValidator;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private LikeValidator likeValidator;

    @Spy
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    @Test
    public void addLikeToPostTest() {
        long userId = 1L;
        Long likeId = 1L;
        Long postId = 1L;

        LikeDto likeDto = LikeDto.builder()
                .postId(postId)
                .userId(userId)
                .build();
//        Like likeToSave = new Like();
        List<Like> likesOfPost = new ArrayList<>();
        Post postOfLike = Post.builder()
                .likes(likesOfPost)
                .build();

        when(userServiceClient.getUser(userId)).thenReturn(null);
        doNothing().when(likeValidator).validateLikeHasTarget(postId, null);

        when(likeRepository.findByPostId(postId)).thenReturn(likesOfPost);
        doNothing().when(likeValidator).validateUserAddOnlyOneLikeToPost(likesOfPost, userId);

        Like likeToCheckComment = new Like();
        when(likeRepository.findById(likeId)).thenReturn(null);
        doNothing().when(likeValidator).validateLikeWasNotPutToComment(likeDto, likeToCheckComment);

        when(postService.getPostEntity(postId)).thenReturn(postOfLike);

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);

        likeService.addLikeToPost(likeDto);

        Like result = captor.capture();
        verify(userServiceClient).getUser(userId);
        verify(likeValidator).validateLikeHasTarget(postId, null);
        verify(likeRepository).findByPostId(postId);
        verify(likeValidator).validateUserAddOnlyOneLikeToPost(likesOfPost, userId);
        verify(likeRepository).findById(likeId);
        verify(likeValidator).validateLikeWasNotPutToComment(likeDto, likeToCheckComment);
        verify(likeRepository).save(result);
        verify(postService).addLikeToPost(postId, result);

        assertTrue(postOfLike.getLikes().contains(result));
        assertEquals(postOfLike, result.getPost());
        assertEquals(userId, result.getUserId());
        assertNotNull(result.getCreatedAt());
    }
}