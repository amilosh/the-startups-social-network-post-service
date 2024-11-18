package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Repository
public class CommentCacheRepository {
    private static final int INCR_DELTA = 1;

    private final RedisTemplate<String, CommentCacheDto> commentCacheRedisTemplate;
    private final RedisTemplate<String, Long> longValueRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisOperations redisOperations;
    private final ZSetRepository zSetRepository;

    @Value("${app.post.cache.news_feed.prefix.post_id}")
    private String postIdPrefix;

    @Value("${app.post.cache.news_feed.prefix.comment_id}")
    private String commentIdPrefix;

    @Value("${app.post.cache.news_feed.prefix.post_comments_ids_set}")
    private String commentsIdsSetPrefix;

    @Value("${app.post.cache.news_feed.postfix.likes}")
    private String likesPostfix;

    @Value("${spring.data.redis.ttl.feed.comment_hour}")
    private int commentTTL;

    @Value("${spring.data.redis.ttl.feed.comment_likes_counter_sec}")
    private int commentLikesCounterTTL;

    @Value("${app.post.cache.news_feed.number_of_comments_limit}")
    private int numberOfCommentsInPostCacheDtoLimit;

    public void save(CommentCacheDto comment) {
        String key = commentKeyBuild(comment.getId());
        redisOperations.executeInMulti(commentCacheRedisTemplate, key, () ->
                commentCacheRedisTemplate.opsForValue().set(key, comment, Duration.ofHours(commentTTL)));

        String setKey = commentsIdsSetKeyBuild(comment.getPostId());
        long timestamp = comment.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        long limit = (numberOfCommentsInPostCacheDtoLimit + 1) * -1;

        zSetRepository.setAndRemoveRange(setKey, key, timestamp, limit);
    }

    public void saveAll(List<CommentCacheDto> comments) {
        comments.forEach(this::save);
    }

    public void deleteById(long id) {
        String key = commentKeyBuild(id);
        commentCacheRedisTemplate.delete(key);
    }

    public List<CommentCacheDto> findAllByPostId(long postId) {
        String setKey = commentsIdsSetKeyBuild(postId);
        Set<String> commentKeys = zSetRepository.getValuesInRange(setKey, 0, numberOfCommentsInPostCacheDtoLimit);

        return commentCacheRedisTemplate.opsForValue().multiGet(commentKeys);
    }

    public void incrementCommentLikes(long commentId) {
        String key = commentLikesCounterKeyBuild(commentId);

        longValueRedisTemplate.opsForValue().increment(key, INCR_DELTA);
        longValueRedisTemplate.expire(key, Duration.ofSeconds(commentLikesCounterTTL));
    }

    public void assignLikesByCounter(String counterKey) {
        assignFieldByCounter(counterKey, (post, likes) -> post.setLikesCount(post.getLikesCount() + likes));
    }

    private void assignFieldByCounter(String counterKey, BiConsumer<CommentCacheDto, Long> consumer) {
        String commentIdKey = getCommentIdKey(counterKey);

        redisOperations.assignFieldByCounter(counterKey, commentIdKey, consumer, commentCacheRedisTemplate,
                Duration.ofHours(commentTTL));
    }

    public Set<String> getCommentLikeCounterKeys() {
        String commentLikeCounterPattern = commentIdPrefix + "*" + likesPostfix;
        return redisTemplate.keys(commentLikeCounterPattern);
    }

    private String commentsIdsSetKeyBuild(long postId) {
        return commentsIdsSetPrefix + postIdPrefix + postId;
    }

    private String commentLikesCounterKeyBuild(long id) {
        return commentKeyBuild(id) + likesPostfix;
    }

    private String commentKeyBuild(long id) {
        return commentIdPrefix + id;
    }

    private String getCommentIdKey(String viewKey) {
        return viewKey.split("/")[0];
    }
}
