package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

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
        List<List<Long>> subLists = getListOfBatches(readyToPublishIds);
        subLists.forEach(postService::processReadyToPublishPosts);
    }

    private List<List<Long>> getListOfBatches(List<Long> list) {
        return IntStream.range(0, list.size())
                .filter(i -> i % postPublishBatchSize == 0)
                .mapToObj(i -> list.subList(i, Math.min(i + postPublishBatchSize, list.size())))
                .toList();
    }
}
