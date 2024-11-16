package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private static final int BATCH_SIZE = 100;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public List<UserDto> getUsersWhoLikePostByPostId(long Id) {
        List<Like> usersWhoLikedPost  = likeRepository.findByPostId(Id);
        return mapLikesToUserDtos(usersWhoLikedPost);
    }


    public List<UserDto> getUsersWhoLikeComments(long id) {
        List<Like> usersWhoLikedComment  = likeRepository.findByCommentId(id);
        return mapLikesToUserDtos(usersWhoLikedComment);
    }


    private List<UserDto> mapLikesToUserDtos(List<Like> usersWhoLiked) {
        List<Long> userIds = usersWhoLiked.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> userIdBatches = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            userIdBatches.add(userIds.subList(i, endIndex));
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (List<Long> batch : userIdBatches) {
            List<UserDto> batchResults = userServiceClient.getUsersByIds(batch);
            userDtos.addAll(batchResults);
        }
        return userDtos;
    }
}
