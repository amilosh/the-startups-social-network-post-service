package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.RedisPostLikeEvent;
import faang.school.postservice.publisher.AbstractJsonRedisPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisPostLikeEventPublisher extends AbstractJsonRedisPublisher<RedisPostLikeEvent> {

    public RedisPostLikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                       ObjectMapper objectMapper,
                                       @Value("${spring.data.redis.channels.post_like_event_channel.name}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
