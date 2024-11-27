package faang.school.postservice.service.impl.feed;

import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl implements RedisCacheService {
    private final RedisTemplate<String, PostRedis> postRedisTemplate;
    private final RedisTemplate<String, UserDto> userRedisTemplate;
    @Value("${spring.data.redis.news-feed.key-prefix.post}")
    private String postPrefix;
    @Value("${spring.data.redis.news-feed.key-prefix.user}")
    private String userPrefix;
    @Value("${spring.data.redis.news-feed.time-to-live}")
    private int ttl;

    @Override
    public void savePost(PostRedis postRedis) {
        postRedisTemplate.opsForValue().set(postPrefix + postRedis.getId(), postRedis, Duration.ofDays(ttl));
        log.info("Post {} was saved in Redis", postRedis.getId());
    }

    @Override
    public void saveUser(UserDto userDto) {
        userRedisTemplate.opsForValue().set(userPrefix + userDto.getId(), userDto, Duration.ofDays(ttl));
        log.info("User {} was saved in Redis", userDto.getId());
    }
}
