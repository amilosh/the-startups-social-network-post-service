package faang.school.postservice.validator.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.AlreadyExistsException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final LikeRepository likeRepository;

    public Like validateLikeExistence(long likeId) {
        return likeRepository.findById(likeId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Like with ID: %d wasn't found", likeId)));
    }

    public void validateUserAddOnlyOneLikeToPost(long postId, long userId) {
        List<Like> likesOfPost = likeRepository.findByPostId(postId);
        validateWasAddedLike(likesOfPost, userId, "post");
    }

    public void validateUserAddOnlyOneLikeToComment(long commentId, long userId) {
        List<Like> likesOfComment = likeRepository.findByCommentId(commentId);
        validateWasAddedLike(likesOfComment, userId, "comment");
    }

    public void validateLikeWasNotPutToComment(LikeDto dto) {
        Like like = likeRepository.findById(dto.getId()).orElse(null);
        validateWasPut(dto.getCommentId(), like, "comment");
    }

    public void validateLikeWasNotPutToPost(LikeDto dto) {
        Like like = likeRepository.findById(dto.getId()).orElse(null);
        validateWasPut(dto.getPostId(), like, "post");
    }

    public void validateThisUserAddThisLike(long userId, long likeId) {
        Like like = validateLikeExistence(likeId);

        if (!like.getUserId().equals(userId)) {
            throw new DataValidationException(
                    String.format("User with ID: %d haven't added Like with ID: %d", userId, likeId));
        }
    }

    private void validateWasPut(Long id, Like like, String placeOfLike) {
        if (id != null ||
                (like != null && like.getComment() != null)) {
            throw new AlreadyExistsException(
                    String.format("This like already was added to %s", placeOfLike));
        }
    }

    private void validateWasAddedLike(List<Like> likes, long userId, String placeOfLike) {
        boolean wasAddedLike = likes.stream()
                .anyMatch(like -> like.getUserId() == userId);
        if (wasAddedLike) {
            throw new DataValidationException(
                    String.format("You can add only one like to one %s", placeOfLike));
        }
    }
}
