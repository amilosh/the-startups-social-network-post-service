package faang.school.postservice.validator.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class LikeValidator {

    private LikeRepository likeRepository;
    private UserServiceClient userServiceClient;

    public boolean validateCommentHasLike(long commentId, long userId) {
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        return like.isEmpty();

    }

    public boolean validatePostHasLike(long postId, long userId) {
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        return like.isEmpty();
    }

    public void validateUserId(Long userId) {
        userServiceClient.getUser(userId);
    }

    public boolean validateCommentHasLikes(long commentId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }

        Set<Long> likedUserIds = Optional.ofNullable(likeRepository.findByCommentId(commentId))
                .orElse(Collections.emptyList())
                .stream()
                .map(Like::getUserId)
                .collect(Collectors.toSet());

        return likedUserIds.containsAll(userIds);
    }


    public boolean validatePostHasLikes(long postId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }

        Set<Long> likedUserIds = Optional.ofNullable(likeRepository.findByPostId(postId))
                .orElse(Collections.emptyList())
                .stream()
                .map(Like::getUserId)
                .collect(Collectors.toSet());

        return likedUserIds.containsAll(userIds);
    }



}
