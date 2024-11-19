package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventForFeedListener implements KafkaEventListener<byte[]> {



    @Override
    public void onMessage(byte[] byteFeedEvent, Acknowledgment acknowledgment) {
        try {
            FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteFeedEvent);
            

        } catch (InvalidProtocolBufferException exception) {
            log.error("Error while parsing feedEvent", exception);
            throw new RuntimeException(exception);
        }
    }
}
