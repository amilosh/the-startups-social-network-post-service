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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorCacheManager {

    private final UserServiceClient userServiceClient;
    private final UserCacheMapper userCacheMapper;
    private final UserRedisRepository userRedisRepository;
    private final UserContext userContext;

    @Value("${spring.data.redis.cache.ttl.user-cache}")
    private Long userCacheTtl;

    @Async("treadPool")
    public void cacheAuthor(Post post) {
        Long authorId = post.getAuthorId();
        caching(authorId);
    }

    @Async("treadPool")
    public void cacheAuthor(Comment comment) {
        Long authorId = comment.getAuthorId();
        caching(authorId);
    }

    private void caching(Long authorId) {
        userContext.setUserId(authorId);
        UserDto author = userServiceClient.getUser(authorId);
        UserCache userCache = userCacheMapper.toUserCache(author);
        userCache.setTtl(userCacheTtl);

        userRedisRepository.save(userCache);
        log.info("Cache save: {}", userCache);
    }
}
