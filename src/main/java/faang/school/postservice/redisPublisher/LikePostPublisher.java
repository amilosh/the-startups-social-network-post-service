package faang.school.postservice.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class LikePostPublisher extends EventPublisher {

    public LikePostPublisher(RedisTemplate<String, Object> redisTemplate,
                             ObjectMapper objectMapper,
                             @Qualifier("likePostChannelTopicAnalytics") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}