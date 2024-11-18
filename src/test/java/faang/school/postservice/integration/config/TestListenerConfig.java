package faang.school.postservice.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static faang.school.postservice.integration.config.TestContainersConfig.redisContainer;

@TestConfiguration
public class TestListenerConfig {

    @Primary
    @Bean
    public JedisConnectionFactory jedisConnectionFactoryTest() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisContainer.getHost(),
                redisContainer.getMappedPort(6379));
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactoryTest,
                                                                       MessageListener postLikeMessageListener,
                                                                       ChannelTopic postLikeTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactoryTest);
        container.addMessageListener(postLikeMessageListener, postLikeTopic);
        return container;
    }

    @Bean
    public ChannelTopic postLikeTopic() {
        return new ChannelTopic("post_like_event");
    }

    @Bean
    public MessageListener postLikeMessageListener() {
        return new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
            }
        };
    }
}
