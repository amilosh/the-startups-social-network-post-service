package faang.school.postservice.aspect;

import faang.school.postservice.cache.UserCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserCacheMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.UserRedisRepository;
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
public class AuthorCachingAspect {
    private final UserServiceClient userServiceClient;
    private final UserCacheMapper userCacheMapper;
    private final UserRedisRepository userRedisRepository;
    private final UserContext userContext;

    @Value("${spring.data.redis.cache.user.ttl}")
    private Long userCacheTtl;

    @AfterReturning(pointcut = "@annotation(AuthorCaching)", returning = "post")
    @Async("treadPool")
    public void afterReturning(Post post) {
        Long authorId = post.getAuthorId();
        toCacheUser(authorId);
    }

    @AfterReturning(pointcut = "@annotation(AuthorCaching)", returning = "comment")
    @Async("treadPool")
    public void afterReturning(Comment comment) {
        Long authorId = comment.getAuthorId();
        toCacheUser(authorId);
    }

    private void toCacheUser(Long authorId) {
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        UserCache userCache = userCacheMapper.toUserCache(author);
        userCache.setTtl(userCacheTtl);

        userRedisRepository.save(userCache);
        log.info("Cache post author: {}", userCache);
    }
}
