package faang.school.postservice.scheduler;

import faang.school.postservice.service.tools.ViewBuffer;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ViewEntity;
import faang.school.postservice.producer.KafkaViewProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SendViewsToKafka {

    private final ViewBuffer viewBuffer;
    private final KafkaViewProducer kafkaViewProducer;

    @Scheduled(cron = "${spring.kafka.scheduler.view-send-message-cron}")
    public void sendEventToKafka() {
        Map<Long, Long> views = viewBuffer.getViewsAndClear();

        views.forEach((postId, viewCount) -> {
            ViewEntity viewEntity = new ViewEntity();
            Post post = new Post();
            post.setId(postId);

            viewEntity.setPost(post);
            viewEntity.setViewCount(viewCount);

            kafkaViewProducer.publish(viewEntity);
        });
    }
}
