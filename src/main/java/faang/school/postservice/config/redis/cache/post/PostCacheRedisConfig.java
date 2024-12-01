package faang.school.postservice.config.redis.cache.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class PostCacheRedisConfig {
    @Bean
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisTemplate<String, String> stringValueRedisTemplate(JedisConnectionFactory connectionFactory,
                                                                  ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, String.class, objectMapper);
    }

    @Bean
    public ZSetOperations<String, String> stringZSetOperations(RedisTemplate<String, String> stringValueRedisTemplate) {
        return stringValueRedisTemplate.opsForZSet();
    }

    @Bean
    public RedisTemplate<String, Long> longValueRedisTemplate(JedisConnectionFactory connectionFactory,
                                                              ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, Long.class, objectMapper);
    }

    @Bean
    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory,
                                                                         ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, PostCacheDto.class, objectMapper);
    }

    @Bean
    public RedisTemplate<String, UserDto> userDtoRedisTemplate(JedisConnectionFactory connectionFactory,
                                                               ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, UserDto.class, objectMapper);
    }

    @Bean
    public RedisTemplate<String, CommentCacheDto> commentCacheRedisTemplate(JedisConnectionFactory connectionFactory,
                                                                            ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, CommentCacheDto.class, objectMapper);
    }

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory connectionFactory, Class<T> clazz,
                                                            ObjectMapper objectMapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
}
