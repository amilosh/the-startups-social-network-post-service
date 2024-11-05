package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJsonRedisPublisher<T> implements RedisEventJsonPublisher<T>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    @Override
    public void publish(T event) {
        try {
            String objectAsJson = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, objectAsJson);
            log.info("Event published to Redis topic {}: {}", topic, objectAsJson);
        } catch (Exception e) {
            log.error("Failed to publish event to Redis", e);
            throw new RuntimeException("Failed to publish event to Redis", e);
        }
    }
}
