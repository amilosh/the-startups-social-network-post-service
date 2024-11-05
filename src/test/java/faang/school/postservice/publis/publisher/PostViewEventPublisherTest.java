package faang.school.postservice.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.PostViewEventDto;
import faang.school.postservice.publisher.PostViewEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostViewEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;

    private PostViewEventDto eventDto;

    @BeforeEach
    public void setUp() {
        eventDto = new PostViewEventDto(1L, 2L, 3L, LocalDateTime.now());
    }

    @Test
    public void testPublishEventDto_CallsConvertAndSend() throws JsonProcessingException {
        when(redisProperties.getPostViewChannel()).thenReturn("postViewChannel");
        when(objectMapper.writeValueAsString(eventDto)).thenReturn("eventAsJson");

        postViewEventPublisher.publish(eventDto);

        verify(objectMapper, times(1)).writeValueAsString(eventDto);
        verify(redisTemplate, times(1)).convertAndSend(eq("postViewChannel"), eq("eventAsJson"));
    }
}


