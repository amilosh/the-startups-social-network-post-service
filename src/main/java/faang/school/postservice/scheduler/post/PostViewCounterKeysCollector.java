package faang.school.postservice.scheduler.post;

import faang.school.postservice.dto.post.message.counter.PostViewCountersKeysMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.PostViewCountersKeysToKafkaPublisher;
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
public class PostViewCounterKeysCollector {
    private final PostViewCountersKeysToKafkaPublisher viewCountersPublisher;
    private final PostCacheRepository postCacheRepository;
    private final EventsPartitioner partitioner;

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_view_counter_collector}")
    public void collectCounters() {
        Set<String> countersKeys = postCacheRepository.getViewCounterKeys();
        List<PostViewCountersKeysMessage> messages =
                partitioner.partitionViewCounterKeysAndMapToMessage(new ArrayList<>(countersKeys));

        messages.forEach(viewCountersPublisher::publish);
    }
}
