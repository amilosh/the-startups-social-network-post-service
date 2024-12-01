package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.CommentCache;
import faang.school.postservice.mapper.comment.CommentCacheMapper;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final ObjectMapper mapper;
    private final CommentCacheMapper commentCacheMapper;
    private final PostRedisRepository postRedisRepository;

    @KafkaListener(topics = "${spring.kafka.topic.comment-publisher}")
    public void consume(String message, Acknowledgment ack) {
        try {
            log.info("Received comment publish message: {}", message);

            CommentPublishMessage commentPublishMessage = mapper.readValue(message, CommentPublishMessage.class);
            Long postId = commentPublishMessage.getPostId();
            CommentCache commentCache = commentCacheMapper.toCommentCache(commentPublishMessage);
            postRedisRepository.addCommentToPost(postId, commentCache);

            ack.acknowledge();

        } catch (JsonProcessingException e) {
            log.error("Error deserializing Kafka message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}