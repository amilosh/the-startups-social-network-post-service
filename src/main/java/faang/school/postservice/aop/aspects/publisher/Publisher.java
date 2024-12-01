package faang.school.postservice.aop.aspects.publisher;

import faang.school.postservice.enums.publisher.PublisherType;
import org.aspectj.lang.JoinPoint;

public interface Publisher {
    PublisherType getType();

    void publish(JoinPoint joinPoint, Object returnedValue);
}
