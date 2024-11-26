package faang.school.postservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisTopicFactory {
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("user_ban");
    }
}
