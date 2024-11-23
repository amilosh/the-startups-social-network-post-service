package faang.school.postservice.redis.service.impl;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.TreeSet;

@Service
@Slf4j
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private long postTtl;

    @Value("${post-comments.size}")
    private int postCommentsSize;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;
    private final RedissonClient redissonClient;

    @Autowired
    public PostCacheServiceImpl(PostCacheRedisRepository postCacheRedisRepository,
                                @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                PostCacheMapper postCacheMapper, RedissonClient redissonClient) {
        this.postCacheRedisRepository = postCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.postCacheMapper = postCacheMapper;
        this.redissonClient = redissonClient;
    }

    @Override
    public void savePostToCache(PostDto post) {
        PostCache postCache = postCacheMapper.toPostCache(post);
        postCache.setVersion(1L);
        postCacheRedisRepository.save(postCache);

        String key = "posts:" + post.getId();
        redisTemplate.expire(key, Duration.ofSeconds(postTtl));
    }

    @Override
    public void updatePostComments(CommentEventKafka event) {
        PostCache postCache = postCacheRedisRepository.findById(event.getPostId())
                .orElseThrow(() -> new NoSuchElementException("Can't find post in redis with id: " + event.getPostId()));
        String lockKey = "lock:" + event.getPostId();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            TreeSet<CommentRedisDto> postComments = postCache.getComments();
            CommentRedisDto commentRedisDto = CommentRedisDto.builder()
                    .postId(event.getPostId()).content(event.getContent())
                    .createdAt(event.getCreatedAt()).authorId(event.getAuthorId()).build();
            if (postComments == null) {
                postComments = new TreeSet<>();
            } else if (postComments.size() == postCommentsSize) {
                postComments.remove(postComments.last());
            }
            postComments.add(commentRedisDto);
            postCache.setComments(postComments);
            postCacheRedisRepository.save(postCache);
        } finally {
            lock.unlock();
        }
    }

    private PostCache updatePostCache(PostDto post) {

    }
}
