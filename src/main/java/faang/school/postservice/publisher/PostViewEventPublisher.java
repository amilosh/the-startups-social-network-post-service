package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.PostViewEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewEventPublisher implements EventPublisher<PostViewEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostViewEventDto event) {
        try {
            String eventAsJson = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(redisProperties.getPostViewChannel(), eventAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PostViewEvent", e);
        }
    }
}