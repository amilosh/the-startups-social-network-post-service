package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.redis.RedisTopicsFactory;
import faang.school.postservice.dto.comment.CommentNotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCommentNotificationEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private Topic commentNotificationTopic;
    @Mock
    private RedisTopicsFactory redisTopicsFactory;

    private RedisCommentNotificationEventPublisher publisher;
    private static final String COMMENT_NOTIFICATION_CHANNEL = "comment_notification_event_channel";

    @BeforeEach
    void setUp() {
        when(redisTopicsFactory.getTopic(COMMENT_NOTIFICATION_CHANNEL))
                .thenReturn(commentNotificationTopic);
        when(commentNotificationTopic.getTopic()).thenReturn(COMMENT_NOTIFICATION_CHANNEL);
        publisher = new RedisCommentNotificationEventPublisher(
                redisTemplate,
                redisTopicsFactory,
                COMMENT_NOTIFICATION_CHANNEL);
    }

    @Test
    void shouldPublishCommentNotificationEvent() {
        CommentNotificationEvent event = new CommentNotificationEvent(
                1L, 2L, 3L, 4L, "Test comment");
        publisher.publishCommentNotificationEvent(event);
        verify(redisTemplate).convertAndSend(eq(COMMENT_NOTIFICATION_CHANNEL), eq(event));
    }
}