package faang.school.postservice.publisher.kafka;

import faang.school.postservice.annotations.SendPostLikeEventToKafka;
import faang.school.postservice.annotations.SendPostUnlikeEventToKafka;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.KafkaPostLikeProducer;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class PostLikeEventKafkaPublisher {
    private final KafkaPostLikeProducer kafkaPostLikeProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendPostLikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendPostLikeEventToKafka sendPostLikeEventToKafka) {
        Like like = (Like) returnValue;
        LikeAction action = sendPostLikeEventToKafka.action();
        kafkaPostLikeProducer.sendPostLikeToKafka(like, action);
    }

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendPostUnlikeEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendPostUnlikeEventToKafka sendPostUnlikeEventToKafka) {
        Post post = (Post) returnValue;
        LikeAction action = sendPostUnlikeEventToKafka.action();
        kafkaPostLikeProducer.sendPostLikeToKafka(post, action);
    }
}
