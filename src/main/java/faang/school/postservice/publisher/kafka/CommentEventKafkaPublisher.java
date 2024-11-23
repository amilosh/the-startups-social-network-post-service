package faang.school.postservice.publisher.kafka;

import faang.school.postservice.kafka.KafkaCommentProducer;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class CommentEventKafkaPublisher {
    private final KafkaCommentProducer kafkaCommentProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.SendCommentCreatedEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue) {
        Comment comment = (Comment) returnValue;
        kafkaCommentProducer.sendCommentToKafka(comment);
    }
}
