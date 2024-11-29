package faang.school.postservice.service.post.redis;

import faang.school.postservice.exception.redis.RedisLockException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.model.post.PostRedis;
import faang.school.postservice.redis.RedisTransactionManager;
import faang.school.postservice.redis.RedisTransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static faang.school.postservice.redis.RedisTransactionResult.LOCK_EXCEPTION;

@RequiredArgsConstructor
@Service
public class PostRedisService {
    private final static String POST_REDIS_KEY = "post:";
    private final static String POST_LIKES_REDIS_KEY = "postLikes:";

    private final PostMapper postMapper;
    private final RedisTemplate<String, PostRedis> postRedisTemplate;
    private final RedisTransactionManager<String, PostRedis> redisTransactionManager;
    private final RedisTemplate<String, Object> commonRedisTemplate;

    public void savePostsToRedis(List<Post> posts) {
        List<PostRedis> postsToRedis = postMapper.mapToPostRedisList(posts);
        for (PostRedis postRedis : postsToRedis) {
            String key = buildKey(postRedis.getId());
            postRedisTemplate.opsForValue().set(key, postRedis);
        }
    }

    public List<PostRedis> findAllByIds(Collection<Long> postIds) {
        List<String> keys = buildKeys(postIds);
        return postRedisTemplate.opsForValue().multiGet(keys);
    }

    @Retryable(retryFor = RedisLockException.class, maxAttempts = 20, backoff = @Backoff(delay = 500))
    public void addComment(Long postId, Long commentId) {
        String key = buildKey(postId);
        RedisTransactionResult redisTransactionResult = redisTransactionManager.updateRedisEntity(key, postRedisTemplate, (postRedis, operations) -> {
            List<Long> comments = postRedis.getComments();
            if (comments == null) {
                comments = new ArrayList<>();
            }
            if (comments.size() >= 3) {
                comments.remove(0);
            }
            comments.add(commentId);
            postRedis.setComments(comments);
            postRedisTemplate.opsForValue().set(key, postRedis);
        });

        if (redisTransactionResult == LOCK_EXCEPTION) {
            throw new RedisLockException("Post %s was updated in concurrent transaction", postId);
        }
    }

    public void changeLikesAmountForPosts(Map<Long, Integer> postLikes) {
        for (Map.Entry<Long, Integer> postLike : postLikes.entrySet()) {
            String key = buildPostLikesKey(postLike.getKey());
            commonRedisTemplate.opsForHash().increment(key, "likes", postLike.getValue());
        }
    }

    @Retryable(retryFor = RedisLockException.class, maxAttempts = 20, backoff = @Backoff(delay = 500))
    public void incrementView(Long postId) {
        String key = buildKey(postId);

        RedisTransactionResult redisTransactionResult = redisTransactionManager.updateRedisEntity(key, postRedisTemplate, (postRedis, operations) -> {
            postRedis.setViews(postRedis.getViews() + 1);
            postRedisTemplate.opsForValue().set(key, postRedis);
        });

        if (redisTransactionResult == LOCK_EXCEPTION) {
            throw new RedisLockException("Post %s was updated in concurrent transaction", postId);
        }
    }

    private String buildKey(Long postId) {
        return POST_REDIS_KEY + postId;
    }

    private List<String> buildKeys(Collection<Long> postIds) {
        return postIds.stream()
                .map(id -> "post:" + id)
                .toList();
    }

    private String buildPostLikesKey(Long postId) {
        return POST_LIKES_REDIS_KEY + postId;
    }
}
