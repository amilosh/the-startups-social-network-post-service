package faang.school.postservice.repository;

import faang.school.postservice.cache.CommentCache;
import faang.school.postservice.cache.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostRedisRepository {
    private final RedisTransactionRetryWrapper<String, PostCache> postCacheRedisTemplateWrapper;

    @Value("${spring.data.redis.cache.post.ttl}")
    private Long ttl;

    @Value("${spring.data.redis.cache.comment.limit}")
    private int commentsLimit;

    public void save(PostCache postCache) {
        String postIdKey = "post-" + postCache.getId();
        postCacheRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(postIdKey);
            operations.multi();
            operations.opsForValue().set(postIdKey, postCache, ttl, SECONDS);
            return operations.exec();
        });
        log.info("Saved post cache with id: {}", postCache.getId());
    }

    public void addCommentToPost(Long postId, CommentCache commentCache) {
        String postIdKey = "post-" + postId;
        postCacheRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(postIdKey);

            PostCache postCache = operations.opsForValue().get(postIdKey);
            postCache.setCommentsCount(postCache.getCommentsCount() + 1);
            List<CommentCache> comments = postCache.getComments();
            if (comments.size() == commentsLimit) {
                comments.remove(commentsLimit - 1);
            }
            comments.add(0, commentCache);

            operations.multi();

            operations.opsForValue().set(postIdKey, postCache, ttl, SECONDS);

            return operations.exec();
        });
        log.info("Saved comment cache to post: {}", postId);
    }

    public Optional<PostCache> findById(Long postId) {
        String postIdKey = "post-" + postId;
        PostCache postCache =  postCacheRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(postIdKey);
            operations.multi();
            operations.opsForValue().get(postIdKey);
            return operations.exec();
        });
        return Optional.ofNullable(postCache);
    }

    public void addLikeToPost(Long postId) {

        String postIdKey = "post-" + postId;
        postCacheRedisTemplateWrapper.executeWithRetry(operations -> {
            operations.watch(postIdKey);
            PostCache postCache = operations.opsForValue().get(postIdKey);
            postCache.setLikeCount(postCache.getLikeCount() + 1);

            operations.multi();

            operations.opsForValue().set(postIdKey, postCache, ttl, SECONDS);
            return operations.exec();
        });
        log.info("Added like to post: {}", postId);
    }
}
