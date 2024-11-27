package faang.school.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.redis.cache.PostFields;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.RedisPostService;
import faang.school.postservice.service.RedisTransactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisPostServiceImpl implements RedisPostService, RedisTransactional {
    private static final String KEY_PREFIX = "post:";

    @Value("${redis.feed.ttl.post:86400}")
    private long postTtlInSeconds;

    @Value("${redis.feed.comment.max-size:3}")
    private int maxRecentComments;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;
    private final RedisPostDtoMapper redisPostDtoMapper;
    private final PostMapper postMapper;

    public RedisPostServiceImpl(
            @Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            PostRepository postRepository,
            RedisPostDtoMapper redisPostDtoMapper,
            PostMapper postMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.postRepository = postRepository;
        this.redisPostDtoMapper = redisPostDtoMapper;
        this.postMapper = postMapper;
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void savePostIfNotExists(RedisPostDto postDto) {
        String key = createKey(postDto.getPostId());
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("Post with ID {} already exists in Redis, skipping...", postDto.getPostId());
            return;
        }
        savePost(postDto);
    }

    @Override
    public RedisPostDto getPost(Long postId) {
        String key = createKey(postId);
        Map<String, Object> postMap = fetchAndCachePostIfAbsent(postId, key);
        return convertMapToPostDto(postMap);
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void addComment(Long postId, String comment) {
        String key = createKey(postId);
        executeRedisTransaction(() -> {
            Map<String, Object> postMap = fetchAndCachePostIfAbsent(postId, key);
            List<String> recentComments = getComments(postMap);
            recentComments.add(0, comment);
            if (recentComments.size() > maxRecentComments) {
                recentComments = recentComments.subList(0, maxRecentComments);
            }
            int commentCount = Integer.parseInt(postMap.getOrDefault(PostFields.COMMENT_COUNT, "0").toString()) + 1;

            redisTemplate.opsForHash().put(key, PostFields.RECENT_COMMENTS, serializeComments(recentComments));
            redisTemplate.opsForHash().put(key, PostFields.COMMENT_COUNT, commentCount);
        });
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void incrementLikesWithTransaction(Long postId) {
        String key = createKey(postId);
        executeRedisTransaction(() -> {
            fetchAndCachePostIfAbsent(postId, key);
            redisTemplate.opsForHash().increment(key, PostFields.LIKE_COUNT, 1);
        });
    }

    private Map<String, Object> fetchAndCachePostIfAbsent(Long postId, String key) {
        Map<Object, Object> postMap = redisTemplate.opsForHash().entries(key);
        if (postMap.isEmpty()) {
            log.warn("Post with ID {} not found in Redis, fetching from database", postId);
            RedisPostDto postFromDb = fetchPostFromDatabase(postId);
            savePost(postFromDb);
            return convertPostDtoToMap(postFromDb);
        }
        return postMap.entrySet().stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey().toString(), e.getValue()), Map::putAll);
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void savePost(RedisPostDto postDto) {
        String key = createKey(postDto.getPostId());
        executeRedisTransaction(() -> {
            Map<String, Object> postMap = convertPostDtoToMap(postDto);
            postMap.forEach((field, value) -> redisTemplate.opsForHash().put(key, field, value));
            updateTtl(key);
        });
    }

    private void updateTtl(String key) {
        redisTemplate.expire(key, postTtlInSeconds, TimeUnit.SECONDS);
    }

    private RedisPostDto fetchPostFromDatabase(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id = %d not found in DB", postId)));
        PostDto postDto = postMapper.toPostDto(post);
        return redisPostDtoMapper.mapToRedisPostDto(postDto);
    }

    private RedisPostDto convertMapToPostDto(Map<String, Object> postMap) {
        RedisPostDto postDto = new RedisPostDto();
        postDto.setPostId(Long.valueOf(postMap.get(PostFields.POST_ID).toString()));
        postDto.setAuthorId(Long.valueOf(postMap.get(PostFields.AUTHOR_ID).toString()));
        postDto.setContent((String) postMap.get(PostFields.CONTENT));
        postDto.setCreatedAt(LocalDateTime.parse((String) postMap.get(PostFields.CREATED_AT)));
        postDto.setCommentCount(Integer.parseInt(postMap.get(PostFields.COMMENT_COUNT).toString()));
        postDto.setLikeCount(Integer.parseInt(postMap.get(PostFields.LIKE_COUNT).toString()));
        postDto.setRecentComments(getComments(postMap));
        return postDto;
    }

    private Map<String, Object> convertPostDtoToMap(RedisPostDto postDto) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put(PostFields.POST_ID, postDto.getPostId().toString());
        postMap.put(PostFields.AUTHOR_ID, postDto.getAuthorId().toString());
        postMap.put(PostFields.CONTENT, postDto.getContent());
        postMap.put(PostFields.CREATED_AT, postDto.getCreatedAt().toString());
        postMap.put(PostFields.COMMENT_COUNT, String.valueOf(postDto.getCommentCount()));
        postMap.put(PostFields.LIKE_COUNT, String.valueOf(postDto.getLikeCount()));
        postMap.put(PostFields.RECENT_COMMENTS, serializeComments(postDto.getRecentComments()));
        return postMap;
    }

    private List<String> getComments(Map<String, Object> postMap) {
        Object commentsObj = postMap.get(PostFields.RECENT_COMMENTS);
        if (commentsObj == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(commentsObj.toString(), new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize comments", e);
            return new ArrayList<>();
        }
    }

    private String serializeComments(List<String> comments) {
        try {
            return objectMapper.writeValueAsString(comments);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize comments, returning empty list", e);
            return "[]";
        }
    }

    private String createKey(Long postId) {
        return KEY_PREFIX + postId;
    }
}