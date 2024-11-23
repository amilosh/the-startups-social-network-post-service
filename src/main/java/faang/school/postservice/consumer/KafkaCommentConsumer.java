package faang.school.postservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.protobuf.generate.CommentEventProto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer implements KafkaConsumer<byte[]> {
    private final CommentEventMapper commentEventMapper;
    private final FeedService feedService;

    @Override
    @KafkaListener(topics = {"${spring.kafka.topics.comments.name}"}, groupId = "first")
    public void processEvent(byte[] message) throws InvalidProtocolBufferException {
        CommentEvent event = commentEventMapper.toEvent(
                CommentEventProto.CommentEvent.parseFrom(message)
        );

    }
}
