package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.repository.cache.util.key.CommentKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class CommentCacheRepository {
    private static final int INCR_DELTA = 1;

    private final RedisTemplate<String, CommentCacheDto> commentCacheRedisTemplate;
    private final RedisTemplate<String, Long> longValueRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTransaction redisTransaction;
    private final RedisOperations redisOperations;
    private final ZSetRepository zSetRepository;
    private final CommentKey commentKey;

    @Value("${spring.data.redis.ttl.feed.comment_hour}")
    private int commentTTL;

    @Value("${spring.data.redis.ttl.feed.comment_likes_counter_sec}")
    private int commentLikesCounterTTL;

    @Value("${app.post.cache.news_feed.number_of_comment_ids_in_post_comments_set}")
    private int numberOfCommentsInPostCacheDtoLimit;

    @Value("${app.post.cache.news_feed.max_index_for_get_comments_in_post_comments_set}")
    private int limitCommentIndex;

    public void save(CommentCacheDto comment) {
        String key = commentKey.build(comment.getId());

        redisTransaction.execute(commentCacheRedisTemplate, key, operations -> {
            operations.multi();
            commentCacheRedisTemplate.opsForValue().set(key, comment, Duration.ofHours(commentTTL));
            return operations.exec();
        });

        String setKey = commentKey.buildPostCommentsSetKey(comment.getPostId());
        long timestamp = comment.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        long limit = numberOfCommentsInPostCacheDtoLimit;

        zSetRepository.setAndRemoveRange(setKey, key, timestamp, limit);
    }

    public void saveAll(List<CommentCacheDto> comments) {
        comments.forEach(this::save);
    }

    public void deleteById(long id) {
        String key = commentKey.build(id);
        CommentCacheDto commentCacheDto = commentCacheRedisTemplate.opsForValue().getAndDelete(key);

        if (commentCacheDto != null) {
            String postsCommentsSetKey = commentKey.buildPostCommentsSetKey(commentCacheDto.getPostId());
            zSetRepository.delete(postsCommentsSetKey, key);
        }
    }

    public List<CommentCacheDto> findAllByPostId(long postId) {
        String setKey = commentKey.buildPostCommentsSetKey(postId);
        Set<String> commentKeys = zSetRepository.getValuesInRange(setKey, 0, limitCommentIndex);

        return commentCacheRedisTemplate.opsForValue().multiGet(commentKeys);
    }

    public void incrementCommentLikes(long commentId) {
        String key = commentKey.buildLikesCounterKey(commentId);

        longValueRedisTemplate.opsForValue().increment(key, INCR_DELTA);
        longValueRedisTemplate.expire(key, Duration.ofSeconds(commentLikesCounterTTL));
    }

    public void assignLikesByCounter(String counterKey) {
        String commentIdKey = commentKey.getCommentKeyFrom(counterKey);

        redisOperations.assignFieldByCounter(counterKey, commentIdKey, commentCacheRedisTemplate,
                Duration.ofHours(commentTTL), (comment, likes) -> comment.setLikesCount(comment.getLikesCount() + likes));
    }

    public Set<String> getCommentLikeCounterKeys() {
        String commentLikeCounterPattern = commentKey.getCommentLikeCounterPattern();
        return redisTemplate.keys(commentLikeCounterPattern);
    }
}
