package faang.school.postservice.publisher.kafka;

import faang.school.postservice.annotations.SendCommentLikeEventToKafka;
import faang.school.postservice.annotations.SendCommentUnlikeEventToKafka;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.KafkaCommentLikeProducer;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class CommentLikeEventKafkaPublisher {
    private final KafkaCommentLikeProducer kafkaCommentLikeProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendCommentLikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendCommentLikeEventToKafka sendCommentLikeEventToKafka) {
        Like like = (Like) returnValue;
        LikeAction action = sendCommentLikeEventToKafka.action();
        kafkaCommentLikeProducer.sendCommentLikeToKafka(like, action);
    }

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendCommentUnlikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendCommentUnlikeEventToKafka sendCommentUnlikeEventToKafka) {
        Comment comment = (Comment) returnValue;
        LikeAction action = sendCommentUnlikeEventToKafka.action();
        kafkaCommentLikeProducer.sendCommentLikeToKafka(comment, action);
    }
}
