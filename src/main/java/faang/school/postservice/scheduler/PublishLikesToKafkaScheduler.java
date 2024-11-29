package faang.school.postservice.scheduler;

import faang.school.postservice.service.like.LikesManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublishLikesToKafkaScheduler {
    private final LikesManager likesManager;

    @Scheduled(cron = "${like.scheduler.cron}")
    public void startPublishLikesToKafka() {
        likesManager.publishPostLikesMapToKafka();
    }
}
