package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import faang.school.postservice.utils.AppCollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    @Value("${post.publisher.batch-size}")
    private Integer postPublishBatchSize;

    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void scheduledPostPublish() {
        List<Long> readyToPublishIds = postService.getReadyToPublishIds();
        List<List<Long>> subLists = AppCollectionUtils.getListOfBatches(readyToPublishIds, postPublishBatchSize);
        subLists.forEach(postService::processReadyToPublishPosts);
    }
}
