package faang.school.postservice.aspect;

import faang.school.postservice.cache.PostCache;
import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.ad.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostCachingAspect {

    private final PostRedisRepository postRedisRepository;
    private final PostCacheMapper postCacheMapper;
    @Value("${spring.data.redis.cache.ttl.post}")
    private Long timeToLive;

    @AfterReturning(pointcut = "@annotation(PostCaching)", returning = "post")
    @Async("treadPool")
    public void cachingPost(Post post) {
        PostCache postCache = postCacheMapper.toPostCache(post);
        postCache.setTtl(timeToLive);
        postRedisRepository.save(postCache);

        log.info("Post '{}' cached", postCache.getId());
        log.info("post exists: {}", postRedisRepository.existsById(postCache.getId()));
    }
}
