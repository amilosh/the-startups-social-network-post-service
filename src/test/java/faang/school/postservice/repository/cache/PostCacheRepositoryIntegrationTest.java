package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.cache.post.PostCacheDto;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import faang.school.postservice.util.BaseContextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PostCacheRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private PostCacheRepositoryImpl postCacheRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisProperties redisProperties;
    private PostCacheDto postCacheDto;

    @BeforeEach
    void setUp() {
        postCacheDto = PostCacheDto.builder()
                .postId(1L)
                .content("This is test")
                .authorId(3L)
                .likesCount(4)
                .build();
    }

    @Test
    @DisplayName("Saving commentCacheDto and then returning it to see if it saved properly")
    public void whenSaveAndFindMethodsCalledThenSerializeAndDeserializeCorrectlyAndStoreInCache() {
        postCacheRepository.save(postCacheDto);
        Optional<PostCacheDto> foundDto = postCacheRepository.findById(postCacheDto.getPostId());
        if (foundDto.isEmpty()) {
            fail();
        }
        long authorId = foundDto.get().getAuthorId();
        assertEquals(authorId, 3);
        long postId = foundDto.get().getPostId();
        assertEquals(postId, 1);
        long likesCount = foundDto.get().getLikesCount();
        assertEquals(likesCount, 4);
        String content = foundDto.get().getContent();
        assertEquals(content, "This is test");
    }

    @Test
    @DisplayName("Testing incrementing like counter by one in existing commentCacheDto in redis")
    public void whenMethodCalledThenIncrementLikeCounterByOne() {
        postCacheRepository.save(postCacheDto);
        boolean isLocked = postCacheRepository.incrementLikesCount(postCacheDto.getPostId());
        assertTrue(isLocked);
        Optional<PostCacheDto> foundDto = postCacheRepository.findById(postCacheDto.getPostId());
        foundDto.ifPresentOrElse(cacheDto -> assertEquals(5, cacheDto.getLikesCount()), Assertions::fail);
    }
}
