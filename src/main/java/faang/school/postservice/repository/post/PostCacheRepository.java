package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.properties.PostCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheProperties postCacheProperties;

    public void cachePost(PostDto postDto) {
        String postId = postDto.getId().toString();
        String setName = postCacheProperties.getSetKey();
        long score = postDto.getPublishedAt().toEpochSecond(ZoneOffset.UTC);

        redisTemplate.opsForValue().set(postId, postDto, postCacheProperties.getLiveTime(),
                postCacheProperties.getTimeUnit());
        redisTemplate.opsForZSet().add(setName, postId, score);
        log.info("post saved to cache: {}", postDto);
    }

    public void cacheAuthorId(Long id) {
        String authorId = id.toString();
        String setName = postCacheProperties.getAuthor().getSetKey();
        long score = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        redisTemplate.opsForValue().set(authorId, authorId, postCacheProperties.getAuthor().getLiveTime(),
                postCacheProperties.getTimeUnit());
        redisTemplate.opsForZSet().add(setName, authorId, score);
        log.info("saved to cache post author id: {}", id);
    }
}
