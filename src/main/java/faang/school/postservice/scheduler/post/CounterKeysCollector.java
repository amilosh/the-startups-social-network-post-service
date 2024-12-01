package faang.school.postservice.scheduler.post;

import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.CommentLikeCounterKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostLikeCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostViewCountersKeysMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.CommentCountersKeysToKafkaPublisher;
import faang.school.postservice.publisher.kafka.publishers.simple.CommentLikeCounterKeysToKafkaPublisher;
import faang.school.postservice.publisher.kafka.publishers.simple.PostLikeCountersKeysToKafkaPublisher;
import faang.school.postservice.publisher.kafka.publishers.simple.PostViewCountersKeysToKafkaPublisher;
import faang.school.postservice.repository.cache.CommentCacheRepository;
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
public class CounterKeysCollector {
    private final CommentLikeCounterKeysToKafkaPublisher commentLikeCountersPublisher;
    private final CommentCountersKeysToKafkaPublisher commentCounterPublisher;
    private final PostLikeCountersKeysToKafkaPublisher likeCountersPublisher;
    private final PostViewCountersKeysToKafkaPublisher viewCountersPublisher;
    private final CommentCacheRepository commentCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final EventsPartitioner partitioner;

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_comment_counter_collector}")
    public void collectCommentCounters() {
        Set<String> countersKeys = postCacheRepository.getCommentCounterKeys();
        List<CommentCountersKeysMessage> messages =
                partitioner.partitionCommentCounterKeysAndMapToMessage(new ArrayList<>(countersKeys));

        messages.forEach(commentCounterPublisher::publish);
    }

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_comment_like_counter_collector}")
    public void collectCommentLikeCounters() {
        Set<String> counterKeys = commentCacheRepository.getCommentLikeCounterKeys();
        List<CommentLikeCounterKeysMessage> messages =
                partitioner.partitionCommentLikeCounterKeysAndMapToMessage(new ArrayList<>(counterKeys));

        messages.forEach(commentLikeCountersPublisher::publish);
    }

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_like_counter_collector}")
    public void collectPostLikeCounters() {
        Set<String> counterKeys = postCacheRepository.getLikeCounterKeys();
        List<PostLikeCountersKeysMessage> messages =
                partitioner.partitionLikeCounterKeysAndMapToMessage(new ArrayList<>(counterKeys));

        messages.forEach(likeCountersPublisher::publish);
    }

    @Scheduled(cron = "${app.post.feed.scheduler.cron.post_view_counter_collector}")
    public void collectPostViewCounters() {
        Set<String> countersKeys = postCacheRepository.getViewCounterKeys();
        List<PostViewCountersKeysMessage> messages =
                partitioner.partitionViewCounterKeysAndMapToMessage(new ArrayList<>(countersKeys));

        messages.forEach(viewCountersPublisher::publish);
    }
}
