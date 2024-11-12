package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AuthorAndPostIdValidator implements ConstraintValidator<AuthorAndPostId, CommentDto> {
    @Override
    public boolean isValid(CommentDto commentDto, ConstraintValidatorContext context) {
        if (commentDto == null) {
            return true;
        }
        boolean hasPostId = commentDto.getPostId() != null;
        boolean hasUserId = commentDto.getAuthorId() != null;
        return hasPostId && hasUserId;
    }
}
