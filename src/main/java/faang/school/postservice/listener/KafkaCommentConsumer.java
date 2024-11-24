package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.repository.NewsFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private ObjectMapper mapper;
    private NewsFeedRepository newsFeedRepository;

    @KafkaListener(topics = "${spring.kafka.topic.comment-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            CommentPublishMessage commentPublishMessage = mapper.readValue(message, CommentPublishMessage.class);
            Long postId = commentPublishMessage.getPostId();
            Long commentAuthorId = commentPublishMessage.getCommentAuthorId();
            log.info("Received comment publish message: postId = {}, commentAuthorId = {}", postId, commentAuthorId);

            ack.acknowledge();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
