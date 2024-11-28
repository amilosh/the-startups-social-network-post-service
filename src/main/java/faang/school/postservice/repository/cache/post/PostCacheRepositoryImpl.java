package faang.school.postservice.repository.cache.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.cache.post.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacheRepositoryImpl implements PostCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(PostCacheDto postCacheDto) {
        redisTemplate.opsForValue().set(String.valueOf(postCacheDto.getPostId()), postCacheDto);
        log.info("save post with id: {} in Redis", postCacheDto.getPostId());
    }

    @Override
    public void incrementLikesCount(Long postId) {
        String key = String.valueOf(postId);
        Object postObject = redisTemplate.opsForValue().get(key);
        if (postObject == null) {
            log.error("Post with id {} not found in Redis", postId);
            throw new IllegalArgumentException("Post not found in cache");
        }
        PostCacheDto postCacheDto = objectMapper.convertValue(postObject, PostCacheDto.class);
        postCacheDto.setLikesCount(postCacheDto.getLikesCount() + 1);
        redisTemplate.opsForValue().set(key, postCacheDto);
        log.debug("Incremented likesCount for post with id: {} to {}", postId, postCacheDto.getLikesCount());
    }

    @Override
    public Optional<PostCacheDto> findById(Long postId) {
        String key = String.valueOf(postId);
        try {
            Object postObject = redisTemplate.opsForValue().get(key);
            PostCacheDto postCacheDto = objectMapper.convertValue(postObject, PostCacheDto.class);
            return Optional.of(postCacheDto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
