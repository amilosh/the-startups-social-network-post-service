package faang.school.postservice.kafka.like;

import faang.school.postservice.kafka.like.event.CommentLikedKafkaEvent;
import faang.school.postservice.kafka.like.event.PostLikedKafkaEvent;
import faang.school.postservice.kafka.post.event.PostViewedKafkaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class UserActionKafkaProducer {
    @Value("${kafka.topic.post-liked-topic}")
    private String postLikedTopic;

    @Value("${kafka.topic.comment-liked-topic}")
    private String commentLikedTopic;

    @Value("${kafka.topic.post-viewed-topic}")
    private String postViewedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostLikesMapToKafka(Map<Long, Integer> postLikes) {
        PostLikedKafkaEvent postLikedKafkaEvent = new PostLikedKafkaEvent(postLikes);
        kafkaTemplate.send(postLikedTopic, postLikedKafkaEvent);
    }

    public void sendCommentLikesMapToKafka(Map<Long, Integer> commentLikes) {
        CommentLikedKafkaEvent commentLikedKafkaEvent = new CommentLikedKafkaEvent(commentLikes);
        kafkaTemplate.send(commentLikedTopic, commentLikedKafkaEvent);
    }

    public void sendPostViewsMapToKafka(Map<Long, Integer> postViews) {
        PostViewedKafkaEvent postViewedKafkaEvent = new PostViewedKafkaEvent(postViews);
        kafkaTemplate.send(postViewedTopic, postViewedKafkaEvent);
    }
}
