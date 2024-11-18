package faang.school.postservice.scheduler.post;

import faang.school.postservice.dto.post.message.counter.PostLikeCountersKeysMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.PostLikeCountersKeysToKafkaPublisher;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.service.feed.util.EventsPartitioner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostLikeCounterKeysCollector {
    private final PostLikeCountersKeysToKafkaPublisher likeCountersPublisher;
    private final PostCacheRepository postCacheRepository;
    private final EventsPartitioner partitioner;

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_like_counter_collector}")
    public void collectCounters() {
        Set<String> counterKeys = postCacheRepository.getLikeCounterKeys();
        List<PostLikeCountersKeysMessage> messages =
                partitioner.partitionLikeCounterKeysAndMapToMessage(new ArrayList<>(counterKeys));

        messages.forEach(likeCountersPublisher::publish);
    }
}
