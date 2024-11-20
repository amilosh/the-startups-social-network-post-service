package faang.school.postservice.publisher.kafka;

import faang.school.postservice.annotations.SendPostViewEventToKafka;
import faang.school.postservice.dto.post.PostFeedResponseDto;
import faang.school.postservice.kafka.KafkaPostViewProducer;
import faang.school.postservice.model.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Setter
@Getter
@RequiredArgsConstructor
@Aspect
@Component
public class PostViewEventKafkaPublisher {
    private final KafkaPostViewProducer kafkaPostViewProducer;

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendPostViewEventToKafka)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendPostViewEventToKafka sendPostViewEventToKafka) {
        Class<?> clazz = sendPostViewEventToKafka.value();
        Class<?> elementType = sendPostViewEventToKafka.elementType();
        if (clazz == Post.class) {
            Post post = (Post) returnValue;
            kafkaPostViewProducer.sendPostViewToKafka(post);
        }
        if (clazz == List.class) {
            if (elementType == Post.class) {
                List<Post> posts = (List<Post>) returnValue;
                kafkaPostViewProducer.sendPostViewsToKafka(posts);
            } else if (elementType == PostFeedResponseDto.class) {
                List<PostFeedResponseDto> posts = (List<PostFeedResponseDto>) returnValue;
                kafkaPostViewProducer.sendPostViewsDtoToKafka(posts);
            }
        }
    }
}
