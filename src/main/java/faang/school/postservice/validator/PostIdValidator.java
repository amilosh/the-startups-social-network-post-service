package faang.school.postservice.validator;

import org.springframework.stereotype.Component;

@Component
public class PostIdValidator {
    public void postIdValidate(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Invalid post ID");
        }
    }
}