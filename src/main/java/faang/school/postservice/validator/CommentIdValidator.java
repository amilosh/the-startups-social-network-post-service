package faang.school.postservice.validator;

import org.springframework.stereotype.Component;

@Component
public class CommentIdValidator {
    public void validateCommentId(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("commentId cannot be null");
        }
    }
}