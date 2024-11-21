package faang.school.postservice.repository.cache;

import faang.school.postservice.properties.cache.CommentCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CommentCacheRepository {
    private final CommentCacheProperties commentCacheProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheCommentAuthor(Long id) {
        String idToCache = id.toString();

        redisTemplate.opsForValue().set(idToCache, idToCache, commentCacheProperties.getLiveTime(),
                commentCacheProperties.getTimeUnit());
        log.info("comment author cached: {}", idToCache);
    }
}
