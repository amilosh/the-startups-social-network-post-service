package faang.school.postservice.validator.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.AlreadyExistsException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    public void validateLikeHasTarget(Long postId, Long commentId) {
        if (postId == null && commentId == null) {
            throw new DataValidationException("Necessary have postId or commentId");
        }
    }

    public void validateUserAddOnlyOneLikeToPost(List<Like> likesOfPost, long userId) {
        validateWasAddedLike(likesOfPost, userId, "post");
    }

    public void validateUserAddOnlyOneLikeToComment(List<Like> likesOfComment, long userId) {
        validateWasAddedLike(likesOfComment, userId, "comment");
    }

    public void validateLikeWasNotPutToComment(LikeDto dto, Like like) {
        if (dto.getCommentId() != null || (like != null && like.getComment() != null)) {
            throw new AlreadyExistsException(("This like already was added to comment"));
        }
    }

    public void validateLikeWasNotPutToPost(LikeDto dto, Like like) {
        if (dto.getPostId() != null || (like != null && like.getPost() != null)) {
            throw new AlreadyExistsException(("This like already was added to post"));
        }
    }

    public void validateThisUserAddThisLike(long userId, Like like) {
        if (!like.getUserId().equals(userId)) {
            throw new DataValidationException(
                    String.format("User with ID: %d haven't added Like with ID: %d", userId, like.getId()));
        }
    }

    private void validateWasAddedLike(List<Like> likes, long userId, String placeOfLike) {
        boolean wasAddedLike = likes.stream()
                .anyMatch(like -> like.getUserId().equals(userId));
        if (wasAddedLike) {
            throw new DataValidationException(
                    String.format("You can add only one like to one %s", placeOfLike));
        }
    }
}
