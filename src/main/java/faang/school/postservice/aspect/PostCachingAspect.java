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

import static faang.school.postservice.mapper.PostCacheMapper.mapCommentToIds;
import static faang.school.postservice.mapper.PostCacheMapper.mapLikeToIds;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostCachingAspect {

    private final PostRedisRepository postRedisRepository;
    private final PostCacheMapper postCacheMapper;
    @Value(value = "${spring.data.redis.cache.ttl.post-cache}")
    private Long timeToLive;

    @AfterReturning(pointcut = "@annotation(PostCaching)", returning = "post")
    @Async("treadPool")
    public void cachingPost(Post post) {
        PostCache postCache = postCacheMapper.toPostCache(post);
        postCache.setLikesIds(mapLikeToIds(post.getLikes()));
        postCache.setCommentIds(mapCommentToIds(post.getComments()));
        postCache.setTtl(timeToLive);

        postRedisRepository.save(postCache);

        log.info("Post '{}' cached {}", postCache.getId(), postCache.getLikesIds());
        log.info("post exists: {}", postRedisRepository.existsById(postCache.getId()));
    }
}
