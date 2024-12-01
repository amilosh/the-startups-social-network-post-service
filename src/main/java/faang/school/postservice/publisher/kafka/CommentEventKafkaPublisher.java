package faang.school.postservice.publisher.kafka;

import faang.school.postservice.kafka.comment.CommentKafkaProducer;
import faang.school.postservice.model.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class CommentEventKafkaPublisher {
    private final CommentKafkaProducer commentKafkaProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.kafka.SendCommentCreatedEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue) {
        Comment comment = (Comment) returnValue;
        commentKafkaProducer.sendCommentToKafka(comment);
    }
}
