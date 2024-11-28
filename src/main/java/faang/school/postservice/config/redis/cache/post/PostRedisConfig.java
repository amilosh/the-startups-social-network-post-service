package faang.school.postservice.config.redis.cache.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.model.post.PostRedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class PostRedisConfig {
    @Bean
    public RedisTemplate<String, PostRedis> postRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, PostRedis> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Jackson2JsonRedisSerializer<PostRedis> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, PostRedis.class);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        return template;
    }
}
