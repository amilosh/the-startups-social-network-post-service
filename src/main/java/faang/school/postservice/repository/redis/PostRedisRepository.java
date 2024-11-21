package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void savePost(PostRedis post, long postId){
        double timeStamp = Instant.now().getEpochSecond();
        redisTemplate.opsForZSet().add(String.valueOf(postId), post, timeStamp);
    }
}
