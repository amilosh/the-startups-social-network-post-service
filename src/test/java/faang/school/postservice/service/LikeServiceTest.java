package faang.school.postservice.service;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    private final CommentRepository commentRepository = mock(CommentRepository.class);

    private final PostRepository postRepository = mock(PostRepository.class);
    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private KafkaLikeProducer kafkaLikeProducer;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("Add like to post: check user exist")
    public void testAddToPostCheckUserExist() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);
        when(userServiceClient.getUser(tempLike.getUserId()))
                .thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToPost(1L, tempLike));
    }

    @Test
    @DisplayName("Add like to post: check post exist")
    public void testAddToPostCheckPostExist() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(postRepository.findById(1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToPost(1L, tempLike));
    }

    @Test
    @DisplayName("Add like to post: check second like")
    public void testAddToPostCheckSecondLike() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        Post post = new Post();
        post.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Like like = new Like();
        when(likeRepository.findByPostIdAndUserId(2L, post.getId())).thenReturn(Optional.of(like));

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToPost(1L, tempLike));
    }

    @Test
    @DisplayName("Add like to post: check execution")
    public void testAddToPost() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        Post post = new Post();
        post.setId(1L);
        post.setLikes(new ArrayList<>());
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(post.getId(), tempLike.getUserId())).thenReturn(Optional.empty());
        when(likeRepository.save(tempLike)).thenReturn(tempLike);

        Like newLike = likeService.addToPost(1L, tempLike);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(kafkaLikeProducer, times(1)).publish(newLike);
    }

    @Test
    @DisplayName("Add like to comment: check user exist")
    public void testAddToCommentCheckUserExist() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);
        when(userServiceClient.getUser(tempLike.getUserId()))
                .thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToComment(1L, tempLike));
    }

    @Test
    @DisplayName("Add like to comment: check comment exist")
    public void testAddToCommentCheckCommentExist() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(commentRepository.findById(1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToComment(1L, tempLike));
    }

    @Test
    @DisplayName("Add like to comment: check second like")
    public void testAddToCommentCheckSecondLike() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        Comment comment = new Comment();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Like like = new Like();
        when(likeRepository.findByCommentIdAndUserId(2L, comment.getId())).thenReturn(Optional.of(like));

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToComment(1L, tempLike));
    }

    @Test
    @DisplayName("Add like to comment: check execution")
    public void testAddToComment() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        Comment comment = new Comment();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Like like = new Like();
        like.setComment(comment);
        when(likeRepository.findByCommentIdAndUserId(2L, comment.getId())).thenReturn(Optional.of(like));

        Assert.assertThrows(RuntimeException.class, () -> likeService.addToComment(1L, tempLike));

        likeRepository.save(like);

        verify(likeRepository, times(1))
                .save(like);

        assertNotNull(like);

        Assert.assertEquals(like.getComment(), comment);
    }

    @Test
    @DisplayName("Remove like from post: check user exist")
    public void testRemoveFromPostCheckUserExist() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);
        when(userServiceClient.getUser(tempLike.getUserId()))
                .thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromPost(1L, 1L));
    }

    @Test
    @DisplayName("Remove like from post: check post exist")
    public void testRemoveFromPostCheckPostExist() {
//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(postRepository.findById(1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromPost(1L, 1L));
    }

    @Test
    @DisplayName("Remove like from post: check like exist")
    public void testRemoveFromPostCheckLikeExist() {
//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(postRepository.existsById(1L)).thenReturn(true);

        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromPost(1L, 1L));
    }

    @Test
    @DisplayName("Remove like from post: check execution")
    public void testRemoveFromPostCheckExecution() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(postRepository.existsById(1L)).thenReturn(true);

        when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(tempLike));

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromPost(1L, 1L));

        likeRepository.deleteByPostIdAndUserId(1L, 1L);

        verify(likeRepository, times(1))
                .deleteByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Remove like from comment: check user exist")
    public void testRemoveFromCommentCheckUserExist() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);
        when(userServiceClient.getUser(tempLike.getUserId()))
                .thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromComment(1L, 1L));
    }

    @Test
    @DisplayName("Remove like from comment: check post exist")
    public void testRemoveFromCommentCheckPostExist() {
//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(commentRepository.existsById(1L)).thenReturn(false);

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromComment(1L, 1L));
    }

    @Test
    @DisplayName("Remove like from comment: check like exist")
    public void testRemoveFromCommentCheckLikeExist() {
//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(commentRepository.existsById(1L)).thenReturn(true);

        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromComment(1L, 1L));
    }

    @Test
    @DisplayName("Remove like from comment: check execution")
    public void testRemoveFromCommentCheckExecution() {
        Like tempLike = new Like();
        tempLike.setUserId(2L);

//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(commentRepository.existsById(1L)).thenReturn(true);

        when(likeRepository.findByCommentIdAndUserId(1L, 1L)).thenReturn(Optional.of(tempLike));

        Assert.assertThrows(RuntimeException.class, () -> likeService.removeFromComment(1L, 1L));

        likeRepository.deleteByCommentIdAndUserId(1L, 1L);

        verify(likeRepository, times(1))
                .deleteByCommentIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Get likes by post: check post exist")
    public void testGetLikesByPostCheckPostExist() {
//        Mockito.when(userServiceClient.getUser(1))
//                .thenReturn(userDto);

        when(postRepository.existsById(1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> likeService.getLikesByPost(1L));
    }

    @Test
    @DisplayName("Get likes by post: check execution")
    public void testGetLikesByPostCheckExecution() {

        Post post = new Post();
        post.setId(2);
        post.setLikes(List.of(new Like(), new Like()));
        when(postRepository.existsById(post.getId())).thenReturn(true);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        int size = likeService.getLikesByPost(post.getId());

        Assert.assertEquals(post.getLikes().size(), size);
    }
}
