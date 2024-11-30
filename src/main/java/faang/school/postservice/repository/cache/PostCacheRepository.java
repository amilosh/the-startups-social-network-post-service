package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.repository.cache.util.key.PostKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostCacheRepository {
    private static final int INCR_DELTA = 1;

    private final RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate;
    private final RedisTemplate<String, Long> longValueRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTransaction redisTransaction;
    private final RedisOperations redisOperations;
    private final PostKey postKey;

    @Value("${spring.data.redis.ttl.feed.post_hour}")
    private int postTTL;

    @Value("${spring.data.redis.ttl.feed.post_views_counter_sec}")
    private int postViewsCounterTTL;

    @Value("${spring.data.redis.ttl.feed.post_likes_counter_sec}")
    private int postLikesCounterTTL;

    @Value("${spring.data.redis.ttl.feed.post_comments_counter_sec}")
    private int commentsCounterTTL;

    public void save(PostCacheDto post) {
        String key = postKey.build(post.getId());
        redisTransaction.execute(postCacheDtoRedisTemplate, key, operations -> {
            operations.multi();
            postCacheDtoRedisTemplate.opsForValue().set(key, post, Duration.ofHours(postTTL));
            return operations.exec();
        });
    }

    public void saveAll(List<PostCacheDto> postDtoList) {
        postDtoList.forEach(this::save);
    }

    public void deleteById(long id) {
        String key = postKey.build(id);
        postCacheDtoRedisTemplate.delete(key);
    }

    public Optional<PostCacheDto> findById(long id) {
        String key = postKey.build(id);
        PostCacheDto postCacheDto = postCacheDtoRedisTemplate.opsForValue().getAndExpire(key, Duration.ofHours(postTTL));

        return Optional.ofNullable(postCacheDto);
    }

    public Optional<PostCacheDto> findByKey(String key) {
        PostCacheDto postCacheDto = postCacheDtoRedisTemplate.opsForValue().getAndExpire(key, Duration.ofHours(postTTL));

        return Optional.ofNullable(postCacheDto);
    }

    public List<PostCacheDto> findAll(Collection<String> keys) {
        Duration duration = Duration.ofHours(postTTL);
        keys.forEach(key -> postCacheDtoRedisTemplate.expire(key, duration));

        return postCacheDtoRedisTemplate.opsForValue().multiGet(keys);
    }

    public Set<String> getViewCounterKeys() {
        String viewCounterKeyPattern = postKey.getViewCounterKeyPattern();
        return redisTemplate.keys(viewCounterKeyPattern);
    }

    public Set<String> getLikeCounterKeys() {
        String likeCounterKeyPattern = postKey.getLikeCounterKeyPattern();
        return redisTemplate.keys(likeCounterKeyPattern);
    }

    public Set<String> getCommentCounterKeys() {
        String commentCounterKeyPattern = postKey.getCommentCounterKeyPattern();
        return redisTemplate.keys(commentCounterKeyPattern);
    }

    public void incrementPostViews(long id) {
        String key = postKey.buildViewsKey(id);
        incrementCounterByKey(key, Duration.ofSeconds(postViewsCounterTTL));
    }

    public void incrementPostLikes(long id) {
        String key = postKey.buildLikesKey(id);
        incrementCounterByKey(key, Duration.ofSeconds(postLikesCounterTTL));
    }

    public void incrementComments(long id) {
        String key = postKey.buildCommentsCounterKey(id);
        incrementCounterByKey(key, Duration.ofSeconds(commentsCounterTTL));
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

    public List<PostCacheDto> filterPostsOnWithoutCache(List<PostCacheDto> postDtoList) {
        return postDtoList.stream()
                .filter(post -> FALSE.equals(redisTemplate.hasKey(postKey.build(post.getId()))))
                .toList();
    }

    public Set<String> mapPostDtoListToPostKeys(List<PostCacheDto> postDtoList) {
        return  postDtoList.stream()
                .map(post -> postKey.build(post.getId()))
                .collect(Collectors.toSet());
    }

    public Set<Long> mapPostDtoListToAuthorsIds(List<PostCacheDto> postDtoList) {
        return postDtoList.stream()
                .flatMap(post -> {
                    Stream<Long> postAuthorId = Stream.of(post.getAuthorId());
                    Stream<Long> commentAuthorIds = post.getComments().stream()
                            .map(CommentCacheDto::getAuthorId);
                    return Stream.concat(postAuthorId, commentAuthorIds);
                })
                .collect(Collectors.toSet());
    }

    private void incrementCounterByKey(String key, Duration duration) {
        longValueRedisTemplate.opsForValue().increment(key, INCR_DELTA);
        longValueRedisTemplate.expire(key, duration);
    }

    private void assignFieldByCounter(String counterKey, BiConsumer<PostCacheDto, Long> consumer) {
        String postIdKey = postKey.getPostKeyFrom(counterKey);

        redisOperations.assignFieldByCounter(counterKey, postIdKey, postCacheDtoRedisTemplate,
                Duration.ofHours(postTTL), consumer);
    }
}
