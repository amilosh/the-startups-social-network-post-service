package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.cache.feed.FeedCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedCacheRepositoryImpl implements FeedCacheRepository {

    private final RedisProperties redisProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Comparator<Long> comparator = Comparator.reverseOrder();

    @Override
    public void save(FeedCacheDto feedCacheDto) {
        redisTemplate.opsForValue()
                .set(String.valueOf(feedCacheDto.getSubscriberId()),
                        feedCacheDto.getPostsIds());
        log.info("save feedCacheDto for user with id: {} in Redis",
                redisTemplate.opsForValue().get(feedCacheDto.getSubscriberId()));
    }

    @Override
    public Optional<FeedCacheDto> findBySubscriberId(Long subscriberId) {
        return Optional.ofNullable((FeedCacheDto) redisTemplate.opsForValue().get(String.valueOf(subscriberId)));
    }

    @Override
    public void addPostId(FeedCacheDto feedCacheDto, Long postId) {
        if (feedCacheDto.getPostsIds().size() >= redisProperties.getMaxPostCountInFeed()) {
            feedCacheDto.setPostsIds(feedCacheDto.getPostsIds().stream()
                    .limit(redisProperties.getMaxPostCountInFeed() - 1)
                    .collect(Collectors.toCollection(() -> new TreeSet<>(comparator))));
        }

        feedCacheDto.getPostsIds().add(postId);
        log.info("add postId: {} in postsIds", postId);
    }
}
