package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class MessageSenderImpl implements MessageSender {

    private final RedisTemplate<Long, String> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void send(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
