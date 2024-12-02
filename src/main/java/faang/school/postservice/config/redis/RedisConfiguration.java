package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort()));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(redisProperties.getTtl().getBasicTtl()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

        RedisCacheConfiguration postCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(redisProperties.getTtl().getPostTtl()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("postCache", postCacheConfig);

        return RedisCacheManager.builder(jedisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .cacheDefaults(defaultCacheConfig)
                .build();
    }

    @Bean
    public ChannelTopic likePostEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel().getName());
    }

    @Bean
    public ChannelTopic postViewEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPostViewChannel().getName());
    }

    @Bean
    public ChannelTopic commentEventChannel() {
        return new ChannelTopic(redisProperties.getChannels().getCommentChannel().getName());
    }

    @Bean
    public ChannelTopic achievementEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel().getName());
    }

    @Bean
    public ChannelTopic publishedCommentEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getNewCommentChannel().getName());
    }

    @Bean
    ChannelTopic userBanTopic() {
        return new ChannelTopic(redisProperties.getChannels().getUserBanChannel().getName());
    }
}
