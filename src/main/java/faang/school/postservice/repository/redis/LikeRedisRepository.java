package faang.school.postservice.repository.redis;

import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.model.redis.LikeRedis;
import faang.school.postservice.model.redis.PostRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LikeRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRedisRepository postRedisRepository;

    public void addLikePost(LikeEvent likeEvent) {
        postRedisRepository.getPost(likeEvent.postId()).ifPresent(post -> {
            LikeRedis likeRedis = LikeRedis.builder()
                    .likeAuthorId(likeEvent.likeAuthorId())
                    .build();
            redisTemplate.opsForSet().add(post.getLikeKey(), likeRedis);
            redisTemplate.expire(post.getLikeKey(), getTimeToLivePost(post));
        });
    }

    public long getAllLikesPost(PostRedis post) {
        Long count = redisTemplate.opsForSet().size(post.getLikeKey());
        return count != null ? count : 0L;
    }

    public void deleteLikePost(LikeEvent likeEvent) {
        postRedisRepository.getPost(likeEvent.postId()).ifPresent(post ->
                redisTemplate.opsForSet().remove(post.getLikeKey(), likeEvent.id()));
    }

    private Duration getTimeToLivePost(PostRedis post) {
        log.info("Get time to live post: {}", post.getTimeToLive());
        return Duration.ofSeconds(post.getTimeToLive());
    }
}
