package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.CommentDto;
import org.springframework.stereotype.Component;

@Component
public class CommentControllerValidator {
    public void validateCommentDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
    }

    public void validatePostId(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post id cannot be null");
        }
    }

    public void validateCommentId(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("Comment id cannot be null");
        }
    }
}
