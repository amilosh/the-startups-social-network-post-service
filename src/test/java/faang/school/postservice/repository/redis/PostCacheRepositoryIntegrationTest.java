package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostCacheRepositoryIntegrationTest extends BaseContextTest {

    @Autowired
    private PostCacheRepository postCacheRepository;
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
                .likeAuthorId(10L)
                .likesCount(4L)
                .commentsCount(2L)
                .build();
    }

    @Test
    @DisplayName("Saving commentCacheDto and then returning it to see if it saved properly")
    public void whenSaveAndFindMethodsCalledThenSerializeAndDeserializeCorrectlyAndStoreInCache() {
        postCacheRepository.save(postCacheDto);
        Optional<PostCacheDto> foundDto = postCacheRepository.findById(postCacheDto.getPostId());
        if (foundDto.isPresent()) {
            long authorId = foundDto.get().getAuthorId();
            assertEquals(authorId, 3);
            long postId = foundDto.get().getPostId();
            assertEquals(postId, 1);
            long likeAuthorId = foundDto.get().getLikeAuthorId();
            assertEquals(likeAuthorId, 10);
            long likesCount = foundDto.get().getLikesCount();
            assertEquals(likesCount, 4);
            long commentsCount = foundDto.get().getCommentsCount();
            assertEquals(commentsCount, 2);
            String content = foundDto.get().getContent();
            assertEquals(content, "This is test");
        }
    }

    @Test
    @DisplayName("Testing incrementing like counter by one in existing commentCacheDto in redis")
    public void whenMethodCalledThenIncrementLikeCounterByOne() {
        postCacheRepository.save(postCacheDto);
        postCacheRepository.incrementLikesCount(postCacheDto.getPostId());

        Optional<PostCacheDto> foundDto = postCacheRepository.findById(postCacheDto.getPostId());
        foundDto.ifPresent(cacheDto -> assertEquals(cacheDto.getLikesCount(), 5));
    }
}
