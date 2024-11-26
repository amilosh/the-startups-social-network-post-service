package faang.school.postservice.publisher.kafka;

import faang.school.postservice.annotations.kafka.SendCommentLikeEventToKafka;
import faang.school.postservice.annotations.kafka.SendCommentUnlikeEventToKafka;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.like.LikeKafkaProducer;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class CommentLikeEventKafkaPublisher {
    private final LikeKafkaProducer likeKafkaProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendCommentLikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendCommentLikeEventToKafka sendCommentLikeEventToKafka) {
        Like like = (Like) returnValue;
        LikeAction action = sendCommentLikeEventToKafka.action();
        likeKafkaProducer.sendCommentLikeToKafka(like, action);
    }

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendCommentUnlikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendCommentUnlikeEventToKafka sendCommentUnlikeEventToKafka) {
        Comment comment = (Comment) returnValue;
        LikeAction action = sendCommentUnlikeEventToKafka.action();
        likeKafkaProducer.sendCommentLikeToKafka(comment, action);
    }
}
