package faang.school.postservice.repository.redis;

import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.model.redis.LikeRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LikeRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRedisRepository postRedisRepository;

    public void addLikePost(LikeEvent likeEvent) {
        postRedisRepository.getPost(likeEvent.postId()).ifPresent(post -> {
            LikeRedis likeRedis = LikeRedis.builder()
                    .key(Constant.LIKE_KEY + likeEvent.id())
                    .likeAuthorId(likeEvent.likeAuthorId())
                    .build();
            redisTemplate.opsForSet().add(post.likeKey(), likeRedis);
            redisTemplate.expire(post.likeKey(), getTimeToLivePost(post));
            log.info("save like post: {}", likeRedis);
        });
    }

    public long getAllLikesPost(PostRedis post) {
        Long count = redisTemplate.opsForSet().size(post.likeKey());
        return count != null ? count : 0L;
    }

    public void deleteLikePost(LikeEvent likeEvent) {
        postRedisRepository.getPost(likeEvent.postId()).ifPresent(post ->
                redisTemplate.opsForSet().remove(post.likeKey(), Constant.LIKE_KEY + likeEvent.id()));
    }

    private Duration getTimeToLivePost(PostRedis post) {
        Long timePost = redisTemplate.getExpire(post.key(), TimeUnit.SECONDS);
        return Duration.ofSeconds(timePost != null ? timePost : 0);
    }
}
