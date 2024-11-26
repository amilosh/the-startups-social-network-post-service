package faang.school.postservice.listener;

import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final CommentRedisRepository commentRedisRepository;

    @KafkaListener(topics = "comment_channel", groupId = "comment_event")
    public void onCommentEvent(CommentEvent comment) {
        commentRedisRepository.addCommentPost(comment);
        log.info("Received comment event: {}", comment);
    }
}
