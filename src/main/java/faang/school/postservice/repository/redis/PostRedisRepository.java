package faang.school.postservice.repository.redis;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    @Value("${spring.data.redis.time-to-live}")
    private long timeToLive;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostMapper postMapper;

    public void savePost(Post post) {

            PostRedis postRedis = postMapper.toRedis(post);
            postRedis.setLikeKey(Constant.LIKE_POST_KEY + post.getId());
            postRedis.setCommentKey(Constant.COMMENT_POST_KEY + post.getId());
            postRedis.setTimeToLive(timeToLive);

    }

    public Optional<PostRedis> getPost(long id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get());
    }
}
