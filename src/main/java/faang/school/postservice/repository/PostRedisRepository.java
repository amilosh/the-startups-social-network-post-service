package faang.school.postservice.repository;

import faang.school.postservice.cache.PostCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import static java.util.concurrent.TimeUnit.SECONDS;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository {
    private final RedisTransactionRetryWrapper<String, PostCache> postCacheRedisTemplateWrapper;

    @Value("${spring.data.redis.cache.post.ttl}")
    private Long ttl;

    public void save(PostCache postCache) {
        String postIdKey = "post-" + postCache.getId();
        postCacheRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(postIdKey);
            operations.multi();
            operations.opsForValue().set(postIdKey, postCache, ttl, SECONDS);
            return operations.exec();
        });
    }
}
