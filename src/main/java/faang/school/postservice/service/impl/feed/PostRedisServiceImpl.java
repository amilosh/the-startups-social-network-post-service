package faang.school.postservice.service.impl.feed;

import faang.school.postservice.exception.RedisTransactionFailedException;
import faang.school.postservice.model.event.kafka.PostCommentEvent;
import faang.school.postservice.model.event.kafka.PostLikeEvent;
import faang.school.postservice.model.event.kafka.PostObservationEvent;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.service.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostRedisServiceImpl implements PostRedisService {
    private final RedisTemplate<String, PostRedis> postRedisRedisTemplate;
    @Value("${spring.data.redis.news-feed.key-prefix.post}")
    private String postPrefix;
    @Value("${spring.data.redis.news-feed.time-to-live}")
    private int ttl;
    @Value("${spring.data.redis.news-feed.post-comments-size}")
    private Integer maxCommentsSize;

    @Override
    @Retryable(maxAttempts = 3, retryFor = RedisTransactionFailedException.class)
    public void saveLikeOnPost(PostLikeEvent event) {
        String key = postPrefix + event.postId();
        PostRedis post = postRedisRedisTemplate.opsForValue().get(key);
        postRedisRedisTemplate.setEnableTransactionSupport(true);
        postRedisRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                try {
                    if (post != null) {
                        long likeCounter = post.getLikes() == null ? 0L : post.getLikes();
                        post.setLikes(++likeCounter);
                        operations.opsForValue().set(key, post);
                        operations.expire(key, Duration.ofDays(ttl));

                        if (operations.exec().isEmpty()) {
                            throw new RedisTransactionFailedException(
                                    "Likes were not incremented for post {}".formatted(event.postId()));
                        }
                    }
                } catch (Exception e) {
                    operations.discard();
                    log.error("Error during incrementing likes to post {}", event.postId());
                    throw e;
                } finally {
                    postRedisRedisTemplate.setEnableTransactionSupport(false);
                }

                return null;
            }
        });
    }

    @Override
    @Retryable(maxAttempts = 3, retryFor = RedisTransactionFailedException.class)
    public void incrementViews(PostObservationEvent event) {
        String key = postPrefix + event.postId();
        PostRedis post = postRedisRedisTemplate.opsForValue().get(key);
        postRedisRedisTemplate.setEnableTransactionSupport(true);
        postRedisRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                try {
                    if (post != null) {
                        long viewCounter = post.getViews() == null ? 0L : post.getViews();
                        post.setViews(++viewCounter);
                        operations.opsForValue().set(key, post);
                        operations.expire(key, Duration.ofDays(ttl));

                        if (operations.exec().isEmpty()) {
                            throw new RedisTransactionFailedException(
                                    "Views were not incremented for post {}".formatted(event.postId()));
                        }
                    }
                } catch (Exception e) {
                    operations.discard();
                    log.error("Error during incrementing views to post {}", event.postId());
                    throw e;
                } finally {
                    postRedisRedisTemplate.setEnableTransactionSupport(false);
                }

                return null;
            }
        });
    }

    @Override
    @Retryable(maxAttempts = 3, retryFor = RedisTransactionFailedException.class)
    public void addComment(PostCommentEvent event) {
        String key = postPrefix + event.postId();
        PostRedis post = postRedisRedisTemplate.opsForValue().get(key);
        postRedisRedisTemplate.setEnableTransactionSupport(true);
        postRedisRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                try {
                    if (post != null) {
                        addCommentWithSizeModeration(post, event);
                        operations.opsForValue().set(key, post);
                        operations.expire(key, Duration.ofDays(ttl));

                        if (operations.exec().isEmpty()) {
                            throw new RedisTransactionFailedException(
                                    "Comment was not added for post {}".formatted(event.postId()));
                        }
                    }
                } catch (Exception e) {
                    operations.discard();
                    log.error("Error during adding comment to post {}", event.postId());
                    throw e;
                } finally {
                    postRedisRedisTemplate.setEnableTransactionSupport(false);
                }

                return null;
            }
        });
    }

    private void addCommentWithSizeModeration(PostRedis post, PostCommentEvent event) {
        LinkedHashSet<PostCommentEvent> comments = post.getComments();
        comments.add(event);

        if (comments.size() > maxCommentsSize) {
            PostCommentEvent oldest = comments.iterator().next();
            comments.remove(oldest);
        }
    }
}
