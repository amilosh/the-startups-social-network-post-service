package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostCacheRepository {
    private static final int INCR_DELTA = 1;

    private final RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate;
    private final RedisTemplate<String, Long> longValueRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisOperations redisOperations;

    @Value("${app.post.cache.news_feed.prefix.post_id}")
    private String postIdPrefix;

    @Value("${app.post.cache.news_feed.postfix.views}")
    private String viewsPostfix;

    @Value("${app.post.cache.news_feed.postfix.likes}")
    private String likesPostfix;

    @Value("${app.post.cache.news_feed.postfix.comments}")
    private String commentsPostfix;

    @Value("${spring.data.redis.ttl.feed.post_hour}")
    private int postTTL;

    @Value("${spring.data.redis.ttl.feed.post_views_counter_sec}")
    private int postViewsCounterTTL;

    @Value("${spring.data.redis.ttl.feed.post_likes_counter_sec}")
    private int postLikesCounterTTL;

    @Value("${spring.data.redis.ttl.feed.post_comments_counter_sec}")
    private int commentsCounterTTL;

    public void save(PostCacheDto post) {
        String key = buildId(post.getId());
        redisOperations.executeInMulti(postCacheDtoRedisTemplate, key, () ->
                postCacheDtoRedisTemplate.opsForValue().set(key, post, Duration.ofHours(postTTL)));
    }

    public void saveAll(List<PostCacheDto> postDtoList) {
        postDtoList.forEach(this::save);
    }

    public void deleteById(long id) {
        postCacheDtoRedisTemplate.delete(buildId(id));
    }

    public List<PostCacheDto> findAll(Collection<String> keys) {
        return postCacheDtoRedisTemplate.opsForValue().multiGet(keys);
    }

    public Set<String> getViewCounterKeys() {
        String viewCounterKeyPattern = postIdPrefix + "*" + viewsPostfix;
        return redisTemplate.keys(viewCounterKeyPattern);
    }

    public Set<String> getLikeCounterKeys() {
        String likeCounterKeyPattern = postIdPrefix + "*" + likesPostfix;
        return redisTemplate.keys(likeCounterKeyPattern);
    }

    public Set<String> getCommentCounterKeys() {
        String commentCounterKeyPattern = postIdPrefix + "*" + commentsPostfix;
        return redisTemplate.keys(commentCounterKeyPattern);
    }

    public void incrementPostViews(long id) {
        String key = postViewIdBuild(id);
        longValueRedisTemplate.opsForValue().increment(key, INCR_DELTA);
        longValueRedisTemplate.expire(key, Duration.ofSeconds(postViewsCounterTTL));
    }

    public void incrementPostLikes(long id) {
        String key = postLikeIdBuild(id);
        longValueRedisTemplate.opsForValue().increment(key, INCR_DELTA);
        longValueRedisTemplate.expire(key, Duration.ofSeconds(postLikesCounterTTL));
    }

    public void incrementComments(long id) {
        String key = commentsCounterKeyBuild(id);
        longValueRedisTemplate.opsForValue().increment(key, INCR_DELTA);
        longValueRedisTemplate.expire(key, Duration.ofSeconds(commentsCounterTTL));
    }

    public void assignViewsByCounter(String counterKey) {
        assignFieldByCounter(counterKey, (post, views) -> post.setViews(post.getViews() + views));
    }

    public void assignLikesByCounter(String counterKey) {
        assignFieldByCounter(counterKey, (post, likes) -> post.setLikesCount(post.getLikesCount() + likes));
    }

    public void assignCommentsByCounter(String counterKey) {
        assignFieldByCounter(counterKey, (post, comments) -> post.setCommentsCount(post.getCommentsCount() + comments));
    }

    private void assignFieldByCounter(String counterKey, BiConsumer<PostCacheDto, Long> consumer) {
        String postIdKey = getPostId(counterKey);

        redisOperations.assignFieldByCounter(counterKey, postIdKey, consumer,
                postCacheDtoRedisTemplate, Duration.ofHours(postTTL));
    }

    private String postViewIdBuild(long id) {
        return buildId(id) + viewsPostfix;
    }

    private String postLikeIdBuild(long id) {
        return buildId(id) + likesPostfix;
    }

    private String commentsCounterKeyBuild(long id) {
        return buildId(id) + commentsPostfix;
    }

    private String buildId(long id) {
        return postIdPrefix + id;
    }

    private String getPostId(String viewKey) {
        return viewKey.split("/")[0];
    }
}
