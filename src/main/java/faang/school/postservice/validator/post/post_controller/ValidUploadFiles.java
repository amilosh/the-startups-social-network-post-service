package faang.school.postservice.validator.post.post_controller;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileUploadValidator.class)
public @interface ValidUploadFiles {

    String message() default "Invalid input files";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
