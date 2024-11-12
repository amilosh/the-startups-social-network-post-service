package faang.school.postservice.validator.comment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AuthorAndPostIdValidator.class})
@Target(ElementType.TYPE)
public @interface AuthorAndPostId {
    String message() default "Should be provided authorId and postId.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
