package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.comment.CommentCacheMapper;
import faang.school.postservice.model.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaProducer<Comment> {

    private final CommentCacheMapper commentCacheMapper;

    @Value("${spring.kafka.topic.comment-publisher}")
    private String topic;

    public KafkaCommentProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                CommentCacheMapper commentCacheMapper) {
        super(kafkaTemplate, objectMapper);
        this.commentCacheMapper = commentCacheMapper;
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected Object createMessage(Comment comment) {
        return commentCacheMapper.toCommentPublishMessage(comment);
    }
}
