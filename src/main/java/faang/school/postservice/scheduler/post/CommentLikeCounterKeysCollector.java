package faang.school.postservice.scheduler.post;

import faang.school.postservice.dto.post.message.counter.CommentLikeCounterKeysMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.CommentLikeCounterKeysToKafkaPublisher;
import faang.school.postservice.repository.cache.CommentCacheRepository;
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
public class CommentLikeCounterKeysCollector {
    private final CommentLikeCounterKeysToKafkaPublisher commentLikeCountersPublisher;
    private final CommentCacheRepository commentCacheRepository;
    private final EventsPartitioner partitioner;

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_comment_like_counter_collector}")
    public void collectCounters() {
        Set<String> counterKeys = commentCacheRepository.getCommentLikeCounterKeys();
        List<CommentLikeCounterKeysMessage> messages =
                partitioner.partitionCommentLikeCounterKeysAndMapToMessage(new ArrayList<>(counterKeys));

        messages.forEach(commentLikeCountersPublisher::publish);
    }
}
