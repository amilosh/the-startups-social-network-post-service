package faang.school.postservice.publisher;

import faang.school.postservice.model.event.BanEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisBanMessagePublisherTest {

    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic channelTopic;

    @InjectMocks
    private RedisBanMessagePublisher redisBanMessagePublisher;

    @Test
    void testPublish() {
        // Arrange
        BanEvent banEvent = new BanEvent(1L);
        when(channelTopic.name()).thenReturn("user_ban");

        // Act
        redisBanMessagePublisher.publish(banEvent);

        // Assert
        verify(redisTemplate, times(1)).send("user_ban", banEvent);
    }
}
