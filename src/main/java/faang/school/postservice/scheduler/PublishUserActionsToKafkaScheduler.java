package faang.school.postservice.scheduler;

import faang.school.postservice.service.counter.CommentLikesCounter;
import faang.school.postservice.service.counter.PostLikesCounter;
import faang.school.postservice.service.counter.PostViewsCounter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublishUserActionsToKafkaScheduler {
    private final CommentLikesCounter commentLikesCounter;
    private final PostLikesCounter postLikesCounter;
    private final PostViewsCounter postViewsCounter;

    @Scheduled(cron = "${scheduler.user-action.comment-like}")
    public void startPublishCommentLikesToKafka() {
        commentLikesCounter.publishCommentLikesMapToKafka();
    }

    @Scheduled(cron = "${scheduler.user-action.post-like}")
    public void startPublishPostLikesToKafka() {
        postLikesCounter.publishPostLikesMapToKafka();
    }

    @Scheduled(cron = "${scheduler.user-action.post-view}")
    public void startPublishPostViewsToKafka() {
        postViewsCounter.publishCommentLikesMapToKafka();
    }
}
