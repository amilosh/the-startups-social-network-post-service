package faang.school.postservice.scheduler.post;

import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.CommentCountersKeysToKafkaPublisher;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.service.feed.util.EventsPartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class CommentCounterKeysCollector {
    private final CommentCountersKeysToKafkaPublisher commentCounterPublisher;
    private final PostCacheRepository postCacheRepository;
    private final EventsPartitioner partitioner;

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_comment_counter_collector}")
    public void collectCounters() {
        Set<String> countersKeys = postCacheRepository.getCommentCounterKeys();
        List<CommentCountersKeysMessage> messages =
                partitioner.partitionCommentCounterKeysAndMapToMessage(new ArrayList<>(countersKeys));

        messages.forEach(commentCounterPublisher::publish);
    }
}
