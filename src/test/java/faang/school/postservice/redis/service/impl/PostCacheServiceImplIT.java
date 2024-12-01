package faang.school.postservice.redis.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.redis.model.entity.AuthorCache;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.SharedTestContainers;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class PostCacheServiceImplIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostCacheRedisRepository postCacheRedisRepository;

    @Autowired
    AuthorCacheRedisRepository authorCacheRedisRepository;

    @Autowired
    PostRepository postRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SharedTestContainers.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", SharedTestContainers.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", SharedTestContainers.POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", SharedTestContainers.POSTGRES_CONTAINER::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("spring.data.redis.host", SharedTestContainers.REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> SharedTestContainers.REDIS_CONTAINER.getMappedPort(6379));
    }

    private final String redisHost = SharedTestContainers.REDIS_CONTAINER.getHost();
    private final Integer redisPort = SharedTestContainers.REDIS_CONTAINER.getMappedPort(6379);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.set("feeds", "9");
            jedis.set("author", "2");
            jedis.set("author", "3");
            jedis.set("posts", "47");
            jedis.set("posts", "46");
            jedis.set("posts", "45");

            jedis.hset("author:2", "_class", "faang.school.postservice.redis.model.entity.AuthorCache");
            jedis.hset("author:2", "email", "janesmith@example.com");
            jedis.hset("author:2", "id", "2");
            jedis.hset("author:2", "username", "JaneSmith");

            jedis.hset("author:3", "_class", "faang.school.postservice.redis.model.entity.AuthorCache");
            jedis.hset("author:3", "email", "jonemith@example.com");
            jedis.hset("author:3", "id", "3");
            jedis.hset("author:3", "username", "JohnSmith");

            jedis.hset("feeds:9", "_class", "faang.school.postservice.redis.model.entity.FeedCache");
            jedis.hset("feeds:9", "id", "9");
            jedis.hset("feeds:9", "postIds.[0]", "47");
            jedis.hset("feeds:9", "postIds.[1]", "46");
            jedis.hset("feeds:9", "postIds.[2]", "45");

            jedis.hset("posts:45", "_class", "faang.school.postservice.redis.model.entity.PostCache");
            jedis.hset("posts:45", "content", "content of post 45");
            jedis.hset("posts:45", "id", "45");
            jedis.hset("posts:45", "numberOfLikes", "0");
            jedis.hset("posts:45", "numberOfViews", "0");
            jedis.hset("posts:45", "publishedAt", "2024-11-27T10:50:00.889178600");
            jedis.hset("posts:45", "authorId", "2");

            jedis.hset("posts:46", "_class", "faang.school.postservice.redis.model.entity.PostCache");
            jedis.hset("posts:46", "content", "content of post 46");
            jedis.hset("posts:46", "id", "46");
            jedis.hset("posts:46", "numberOfLikes", "0");
            jedis.hset("posts:46", "numberOfViews", "0");
            jedis.hset("posts:46", "publishedAt", "2024-11-27T10:50:01.889178600");
            jedis.hset("posts:46", "authorId", "2");

            jedis.hset("posts:47", "_class", "faang.school.postservice.redis.model.entity.PostCache");
            jedis.hset("posts:47", "content", "content of post 47");
            jedis.hset("posts:47", "id", "47");
            jedis.hset("posts:47", "numberOfLikes", "0");
            jedis.hset("posts:47", "numberOfViews", "0");
            jedis.hset("posts:47", "publishedAt", "2024-11-27T10:50:03.889178600");
            jedis.hset("posts:47", "authorId", "3");
        }
    }

    @Test
    @DisplayName("Should add like to post in cash/repository and increment number of likes")
    public void testAddLike_Success() throws Exception {

        LikeDto likeDto = new LikeDto();
        likeDto.setPostId(45L);
        likeDto.setUserId(3L);

        UserDto userDto = new UserDto();
        userDto.setId(3L);
        userDto.setEmail("johndoe@example.com");
        userDto.setUsername("JohnDoe");
        when(userServiceClient.getUser(3L)).thenReturn(userDto);

        String filterJson = objectMapper.writeValueAsString(likeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/likes/post/{postId}", 45L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson)
                        .header("x-user-id", likeDto.getUserId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.postId").value(45));

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .until(() -> {
                        String postData = jedis.hget("posts:45", "id");
                        return postData != null;
                    });
        }

        Post post = postRepository.findById(45L).orElse(null);
        assert post != null;
        PostCache postCache = postCacheRedisRepository.findById(45L).orElse(null);
        assert postCache != null;

        assertAll(
                () -> assertEquals(1, postCache.getNumberOfLikes()),
                () -> assertEquals(likeDto.getUserId(), postCache.getLikes().get(0).getUserId()),
                () -> assertEquals(likeDto.getPostId(), postCache.getLikes().get(0).getPostId()),
                () -> assertEquals(1, post.getLikes().size()),
                () -> assertEquals(likeDto.getUserId(), post.getLikes().get(0).getUserId()),
                () -> assertEquals(1, post.getNumberOfLikes())
        );
    }

    @Test
    @Transactional
    @DisplayName("Should increment number of views in cash/repository")
    public void testAddPostView_Success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}", 45L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(45))
                .andExpect(jsonPath("$.content").value("content of post 45"));

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .until(() -> {
                        String postData = jedis.hget("posts:45", "id");
                        return postData != null;
                    });
        }

        Post post = postRepository.findById(45L).orElse(null);
        assert post != null;
        PostCache postCache = postCacheRedisRepository.findById(45L).orElse(null);
        assert postCache != null;

        assertAll(
                () -> assertEquals(1, postCache.getNumberOfViews()),
                () -> assertEquals(1, post.getNumberOfViews())
        );
    }

    @Test
    @DisplayName("Should add comment and push out the last one if more than three comments exist")
    public void testUpdatePostComments_Success() throws Exception {

        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(45L);
        commentDto.setAuthorId(3L);

        UserDto userDto = new UserDto();
        userDto.setId(3L);
        userDto.setEmail("johndoe@example.com");
        userDto.setUsername("JohnDoe");
        when(userServiceClient.getUser(3L)).thenReturn(userDto);

        for (int i = 1; i < 5; i++) {
            commentDto.setContent("content of comment no. " + i + " for post 45");

            String filterJson = objectMapper.writeValueAsString(commentDto);

            mockMvc.perform(MockMvcRequestBuilders.post("/comment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(filterJson)
                            .header("x-user-id", commentDto.getAuthorId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.postId").value(45))
                    .andExpect(jsonPath("$.authorId").value(3))
                    .andExpect(jsonPath("$.content").value("content of comment no. " + i + " for post 45"));

            try (Jedis jedis = new Jedis(redisHost, redisPort)) {
                Awaitility.await()
                        .atMost(Duration.ofSeconds(5))
                        .until(() -> {
                            String postData = jedis.hget("posts:45", "id");
                            return postData != null;
                        });
            }
        }

        Post post = postRepository.findById(45L).orElse(null);
        assert post != null;
        PostCache postCache = postCacheRedisRepository.findById(45L).orElse(null);
        assert postCache != null;
        AuthorCache author = authorCacheRedisRepository.findById(3L).orElse(null);
        assert author != null;

        assertAll(
                () -> assertEquals(3, postCache.getComments().size()),
                () -> assertEquals("johndoe@example.com", author.getEmail()),
                () -> assertTrue(
                        postCache.getComments().stream()
                                .noneMatch(comment -> comment.
                                        getContent().contains("content of comment no. 1 for post 45")))

        );

    }
}
