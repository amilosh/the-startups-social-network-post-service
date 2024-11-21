package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewPostEventListener implements KafkaEventListener<byte[]> {

    private final CacheRepository<Long> cacheRepository;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.view_post.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "messageListenerContainer")
    public void onMessage(byte[] byteEvent, Acknowledgment acknowledgment) {
        try {
            FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteEvent);
            String stringPostId = Long.toString(feedEvent.getPostId());
            long authorId = feedEvent.getAuthorId();
            cacheRepository.save(stringPostId, authorId);
            acknowledgment.acknowledge();

        } catch (InvalidProtocolBufferException exception) {
            throw new RuntimeException(exception);
        }
    }
}
