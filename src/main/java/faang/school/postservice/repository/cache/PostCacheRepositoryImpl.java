package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.PostCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacheRepositoryImpl implements PostCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(PostCacheDto postCacheDto) {
        redisTemplate.opsForValue().set(String.valueOf(postCacheDto.getPostId()), postCacheDto);
        log.info("save post with id: {} in Redis",
                redisTemplate.opsForValue().get(String.valueOf(postCacheDto.getPostId())));
    }
}
