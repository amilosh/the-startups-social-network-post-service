package faang.school.postservice.validator.resource;

import faang.school.postservice.dto.resource.ResourceType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ResourceFileSizeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidResourceFileSize {
    String message() default "File size exceeds the allowed limit for the resource type.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long defaultMaxSizeInBytes() default 100 * 1024 * 1024;

    ResourceType[] applicableTypes() default {ResourceType.IMAGE, ResourceType.OTHER};
}