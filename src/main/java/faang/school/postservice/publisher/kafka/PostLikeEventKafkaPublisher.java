package faang.school.postservice.publisher.kafka;

import faang.school.postservice.annotations.kafka.SendPostLikeEventToKafka;
import faang.school.postservice.annotations.kafka.SendPostUnlikeEventToKafka;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.like.LikeKafkaProducer;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class PostLikeEventKafkaPublisher {
    private final LikeKafkaProducer likeKafkaProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendPostLikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendPostLikeEventToKafka sendPostLikeEventToKafka) {
        Like like = (Like) returnValue;
        LikeAction action = sendPostLikeEventToKafka.action();
        likeKafkaProducer.sendPostLikeToKafka(like, action);
    }

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendPostUnlikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendPostUnlikeEventToKafka sendPostUnlikeEventToKafka) {
        Post post = (Post) returnValue;
        LikeAction action = sendPostUnlikeEventToKafka.action();
        likeKafkaProducer.sendPostLikeToKafka(post, action);
    }
}
