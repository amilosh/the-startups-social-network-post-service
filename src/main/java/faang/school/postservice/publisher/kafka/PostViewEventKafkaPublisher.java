package faang.school.postservice.publisher.kafka;

import faang.school.postservice.annotations.kafka.SendPostViewEventToKafka;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.kafka.post.PostKafkaProducer;
import faang.school.postservice.model.post.Post;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Aspect
@Component
public class PostViewEventKafkaPublisher {
    private final PostKafkaProducer postKafkaProducer;

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
            postKafkaProducer.sendPostViewToKafka(post);
        }
        if (clazz == List.class) {
            if (elementType == Post.class) {
                List<Post> posts = (List<Post>) returnValue;
                postKafkaProducer.sendPostViewsToKafka(posts);
            } else if (elementType == PostFeedResponseDto.class) {
                List<PostFeedResponseDto> posts = (List<PostFeedResponseDto>) returnValue;
                postKafkaProducer.sendPostViewsDtoToKafka(posts);
            }
        }
    }
}
