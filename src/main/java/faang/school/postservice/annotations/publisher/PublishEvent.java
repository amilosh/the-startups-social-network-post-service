package faang.school.postservice.annotations.publisher;

import faang.school.postservice.enums.publisher.PublisherType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishEvent {
    PublisherType type();
}
