package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeService likeService;

    @Test
    void testGetUsersWhoLikePostByPostId_NoLikes() {
        when(likeRepository.findByPostId(anyLong())).thenReturn(List.of());

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(1L);

        assertTrue(result.isEmpty(), "Result should be empty when there are no likes");
        verify(likeRepository, times(1)).findByPostId(1L);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void testGetUsersWhoLikePostByPostId_SingleBatch() {
        long postId = 1L;

        Post post = new Post();
        post.setId(postId);

        Like like1 = new Like();
        like1.setId(1L);
        like1.setPost(post);
        like1.setUserId(1L);

        Like like2 = new Like();
        like2.setId(2L);
        like2.setPost(post);
        like2.setUserId(2L);

        List<Like> likes = List.of(like1, like2);

        List<UserDto> userDtos = List.of(
                new UserDto(1L, "User1", "user1@test.com"),
                new UserDto(2L, "User2", "user2@test.com")
        );

        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(List.of(1L))).thenReturn(List.of(userDtos.get(0)));
        when(userServiceClient.getUsersByIds(List.of(2L))).thenReturn(List.of(userDtos.get(1)));

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(postId);

        assertEquals(userDtos, result, "Result should match the expected user DTOs");
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L));
        verify(userServiceClient, times(1)).getUsersByIds(List.of(2L));
    }

    @Test
    void testGetUsersWhoLikeComments_SingleBatch() {
        long commentId = 1L;
        List<Long> userIds = List.of(1L, 2L);
        List<Like> likes = createLikes(userIds);
        List<UserDto> userDtos = createUserDtos(userIds);

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(List.of(1L))).thenReturn(List.of(userDtos.get(0)));
        when(userServiceClient.getUsersByIds(List.of(2L))).thenReturn(List.of(userDtos.get(1)));

        List<UserDto> result = likeService.getUsersWhoLikeComments(commentId);

        assertEquals(userDtos, result, "Result should match the user DTOs");
        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, times(2)).getUsersByIds(anyList());
    }

    @Test
    void testGetUsersWhoLikePostByPostId_MultipleBatches() {
        long postId = 1L;
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= 150; i++) {
            userIds.add(i);
        }

        List<Like> likes = createLikes(userIds);
        List<UserDto> userDtos = createUserDtos(userIds);

        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenAnswer(invocation -> {
            List<Long> ids = invocation.getArgument(0);
            if (ids.size() == 100) {
                return userDtos.subList(0, 100);
            } else if (ids.size() == 50) {
                return userDtos.subList(100, 150);
            } else {
                return ids.stream().map(id -> userDtos.stream().filter(dto -> dto.getId().equals(id)).findFirst().orElse(null)).collect(Collectors.toList());
            }
        });

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(postId);

        assertEquals(150, result.size(), "Result size should match the number of likes");
        assertEquals(userDtos, result, "Result should match the user DTOs");
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, atLeastOnce()).getUsersByIds(anyList());
    }

    private List<UserDto> createUserDtos(List<Long> userIds) {
        return userIds.stream()
                .map(id -> UserDto.builder()
                        .id(id)
                        .username("User" + id)
                        .email("user" + id + "@test.com")
                        .build())
                .toList();
    }

    private List<Like> createLikes(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> {
                    Like like = new Like();
                    like.setUserId(userId);
                    return like;
                })
                .toList();
    }

}