package faang.school.postservice.redis.service.impl;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private long postTtl;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;

    @Autowired
    public PostCacheServiceImpl(PostCacheRedisRepository postCacheRedisRepository,
                                @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                PostCacheMapper postCacheMapper) {
        this.postCacheRedisRepository = postCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.postCacheMapper = postCacheMapper;
    }

    @Override
    public void savePostToCache(PostDto post) {
        PostCache postCache = postCacheMapper.toPostCache(post);
        postCacheRedisRepository.save(postCache);

        String key = "posts:" + post.getId();
        redisTemplate.expire(key, Duration.ofSeconds(postTtl));
    }
}
