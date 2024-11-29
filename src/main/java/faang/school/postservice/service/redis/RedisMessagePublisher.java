package faang.school.postservice.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final StringRedisTemplate redisTemplate;

    private final String redisChannel;

    public void sendMessage(String message) {
        redisTemplate.convertAndSend(redisChannel, message);
    }

}
