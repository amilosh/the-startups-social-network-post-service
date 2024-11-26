package faang.school.postservice.repository.redis;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRedisRepository {

    @Value("${spring.data.redis.time-to-live:86400}")
    private long timeToLive;

    private final RedisTemplate<String, Object> redisTemplate;

    public void savePost(Post post) {
        PostRedis postRedis = PostRedis.builder()
                .key(Constant.POST_KEY + post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .commentKey(Constant.COMMENT_POST_KEY + post.getId())
                .likeKey(Constant.LIKE_POST_KEY + post.getId())
                .build();

        redisTemplate.opsForValue().set(postRedis.key(), postRedis);
        redisTemplate.expire(postRedis.key(), Duration.ofSeconds(timeToLive));
    }

    public Optional<PostRedis> getPost(long postId) {
        return Optional.ofNullable((PostRedis) redisTemplate.opsForValue().get(Constant.POST_KEY + postId));
    }

    public List<PostRedis> getPostsByIds(List<String> postKeys) {
        List<Object> results = redisTemplate.opsForValue().multiGet(postKeys);
        return results != null ? results.stream()
                .filter(Objects::nonNull)
                .map(result -> (PostRedis) result)
                .toList() : Collections.emptyList();
    }

    public void deletePost(long postId) {
        getPost(postId).ifPresent(post -> {
            redisTemplate.opsForSet().remove(post.likeKey());
            redisTemplate.opsForZSet().remove(post.commentKey());
            redisTemplate.delete(Constant.POST_KEY + postId);
            log.info("Deleted post with id {}", post);
        });
    }
}

