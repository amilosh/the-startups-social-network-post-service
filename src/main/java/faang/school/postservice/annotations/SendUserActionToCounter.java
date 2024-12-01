package faang.school.postservice.annotations;

import faang.school.postservice.service.counter.enumeration.ChangeType;
import faang.school.postservice.service.counter.enumeration.UserAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendUserActionToCounter {
    UserAction userAction();

    ChangeType changeType();

    Class<?> type();

    Class<?> collectionElementType() default Object.class;
}
