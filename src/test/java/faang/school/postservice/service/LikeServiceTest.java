package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Spy
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeService likeService;

    @Test
    void testGetUsersWhoLikePostByPostId_NoLikes() {
        when(likeRepository.findByPostId(anyLong())).thenReturn(List.of());

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(1L);

        assertTrue(result.isEmpty());
        verify(likeRepository).findByPostId(1L);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void testGetUsersWhoLikePostByPostId() {
        long postId = 1L;
        List<Long> userIds = List.of(1L, 2L);
        List<Like> likes = createLikes(userIds);
        List<UserDto> userDtos = createUserDtos(userIds);

        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        mockUserServiceClient(userIds, userDtos);

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(postId);

        assertEquals(userDtos, result);
        verify(likeRepository).findByPostId(postId);
    }

    @Test
    void testGetUsersWhoLikeComments() {
        long commentId = 1L;
        List<Long> userIds = List.of(1L, 2L);
        List<Like> likes = createLikes(userIds);
        List<UserDto> userDtos = createUserDtos(userIds);

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);
        mockUserServiceClient(userIds, userDtos);

        List<UserDto> result = likeService.getUsersWhoLikeComments(commentId);

        assertEquals(userDtos, result);
        verify(likeRepository).findByCommentId(commentId);
    }

    @Test
    void testGetUsersWhoLikePostByPostId_MultipleBatches() {
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= 150; i++) {
            userIds.add(i);
        }

        List<Like> likes = createLikes(userIds);
        List<UserDto> userDtos = createUserDtos(userIds);

        when(likeRepository.findByPostId(anyLong())).thenReturn(likes);
        mockUserServiceClient(userIds, userDtos);

        List<UserDto> result = likeService.getUsersWhoLikePostByPostId(1L);

        assertEquals(150, result.size());
        verify(likeRepository).findByPostId(1L);
        verify(userServiceClient, times(2)).getUsersByIds(anyList());
    }

    private List<UserDto> createUserDtos(List<Long> userIds) {
        return userIds.stream()
                .map(id -> UserDto.builder().id(id).username("User" + id).email("user" + id + "@test.com").build())
                .toList();
    }

    private List<Like> createLikes(List<Long> userIds) {
        return userIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setUserId(id);
                    return like;
                })
                .toList();
    }


    private void mockUserServiceClient(List<Long> userIds, List<UserDto> userDtos) {
        when(userServiceClient.getUsersByIds(anyList())).thenAnswer(invocation -> {
            List<Long> batchIds = invocation.getArgument(0);
            return userDtos.stream()
                    .filter(dto -> batchIds.contains(dto.getId()))
                    .toList();
        });
    }
}
