package faang.school.postservice.redis.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.model.entity.AuthorCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.redis.service.AuthorCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class AuthorCacheServiceImpl implements AuthorCacheService {

    @Value("${cache.author-ttl:86400}")
    private long authorTtl;

    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    @Autowired
    public AuthorCacheServiceImpl (AuthorCacheRedisRepository authorCacheRedisRepository,
                                   @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                   AuthorCacheMapper authorCacheMapper, 
                                   UserServiceClient userServiceClient) {

        this.authorCacheRedisRepository = authorCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.authorCacheMapper = authorCacheMapper;
        this.userServiceClient = userServiceClient;
    }
    
    public void saveAuthorToCache(Long postAuthorId) {
        UserDto author = userServiceClient.getUser(postAuthorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(author);
        authorCacheRedisRepository.save(authorCache);

        String key = "author:" + authorCache.getId();
        redisTemplate.expire(key, Duration.ofSeconds(authorTtl));
    }
}
