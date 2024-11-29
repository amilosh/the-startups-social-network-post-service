package faang.school.postservice.annotations;

import faang.school.postservice.dto.like.LikeAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendPostLikeToLikesManager {
    Class<?> type();

    LikeAction action();
}