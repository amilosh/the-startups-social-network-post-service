package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventForFeedListener implements KafkaEventListener<byte[]> {

    private final CacheRepository<byte[]> cacheRepository;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.comment-for-feed.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "messageListenerContainer")
    public void onMessage(byte[] byteFeedEvent, Acknowledgment acknowledgment) {
        try {
            FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteFeedEvent);
            String postId = Long.toString(feedEvent.getPostId());
            cacheRepository.save(postId, byteFeedEvent);
            acknowledgment.acknowledge();

        } catch (InvalidProtocolBufferException exception) {
            log.error("Error while parsing feedEvent", exception);
            throw new RuntimeException(exception);
        }
    }
}
