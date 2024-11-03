package faang.school.postservice.publis.aspect.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.publis.publisher.PostEventPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PostEventPublishAspect {
    private final PostEventPublisher publisher;

    @Pointcut("@annotation(PostEventPublish)")
    public void postEventPublishMethods() {}

    @AfterReturning(pointcut = "postEventPublishMethods()", returning = "post")
    public void afterReturningAdvice(Post post) {
        publisher.publish(post);
    }
}