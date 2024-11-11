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

    public void validateLikeExistence(long likeId) {
        likeRepository.findById(likeId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Like with ID: %d wasn't found", likeId)));
    }

    public void validateUserAddOnlyOneLikeToPost(long postId, long userId) {
        List<Like> likesOfPost = likeRepository.findByPostId(postId);
        boolean wasAddedLike = likesOfPost.stream()
                .anyMatch(like -> like.getUserId() == userId);
        if (wasAddedLike) {
            throw new DataValidationException("You can add only one like to one post");
        }
    }

    public void validateLikeWasNotPutToComment(LikeDto dto) {
        Like like = likeRepository.findById(dto.getId()).orElse(null);
        if (dto.getCommentId() != null ||
                (like != null && like.getComment() != null)) {
            throw new AlreadyExistsException("This like already was added to comment");
        }
    }
}
