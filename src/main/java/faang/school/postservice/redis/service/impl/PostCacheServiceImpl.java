package faang.school.postservice.redis.service.impl;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${cache.post-ttl}")
    private long postTtl;

    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;

    @Autowired
    public PostCacheServiceImpl(PostCacheRedisRepository postCacheRedisRepository,
                                @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                PostCacheMapper postCacheMapper) {
        this.postCacheRedisRepository = postCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.postCacheMapper = postCacheMapper;
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
    public PostCache updatePostComments(Long id) {

        // TODO make tests

        PostCache postCache = postCacheRedisRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post does not exist in Redis"));
        System.out.println("333333333333333333 " + postCache);

        String key = "posts:"+id.toString();
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkk"+key);
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkk"+key.getBytes());

        redisTemplate.execute((RedisConnection connection) -> {
            connection.watch(key.getBytes()); // Следим за данным ключом

            // Получаем текущие данные объекта PostCache
            Map<byte[], byte[]> data = connection.hGetAll(key.getBytes());
            System.out.println("ddddddddddddddddddddddd "+data);
            System.out.println("asdfasdfasdf"+ Arrays.toString(data.get("version")));
            if (data.isEmpty()) {
                connection.unwatch(); // Если данных нет, снимаем наблюдение
                throw new NoSuchElementException("Post does not exist in Redis");
            }

            // Извлекаем текущую версию
            Long currentVersion = Long.parseLong(new String(data.get("version".getBytes())));

            // Проверяем версию
            if (!currentVersion.equals(postCache.getVersion())) {
                connection.unwatch(); // Если версии не совпадают, отменяем операцию
                throw new IllegalStateException("PostCache was modified by another transaction.");
            }

            // Если версии совпадают, начинаем транзакцию
            connection.multi(); // Начинаем транзакцию

            // Обновляем данные
            String newContent = "Updated content";
            connection.hSet(key.getBytes(), "content".getBytes(), newContent.getBytes());
            connection.hSet(key.getBytes(), "version".getBytes(), String.valueOf(currentVersion + 1).getBytes()); // Увеличиваем версию

            // Выполняем транзакцию
            List<Object> result = connection.exec();

            // Проверяем, была ли выполнена транзакция
            return result != null && !result.isEmpty() ? postCache : null;
        });

        return null;
    }
}
