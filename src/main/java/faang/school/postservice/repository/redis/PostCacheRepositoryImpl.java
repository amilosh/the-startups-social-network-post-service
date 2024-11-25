package faang.school.postservice.repository.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public class PostCacheRepositoryImpl implements PostCacheRepositoryCustom {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "Post:";

    @Override
    public void incrementLikesCount(Long postId) {
        String key = KEY_PREFIX + postId;
        BoundHashOperations<String, String, Object> hashOps = redisTemplate.boundHashOps(key);
        Object currentLikesObj = hashOps.get("likesCount");
        assert currentLikesObj != null;
        long currentLikes = ((Number) currentLikesObj).longValue();
        hashOps.put("likesCount", currentLikes + 1);
        log.debug("New like counter is : {}", currentLikes + 1);
    }
}
