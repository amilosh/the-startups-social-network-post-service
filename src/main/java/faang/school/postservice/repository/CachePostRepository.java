package faang.school.postservice.repository;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CachePostRepository implements CacheRepository<PostDto> {

    private final RedisCacheService<PostDto> redisCacheService;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    public void save(String key, PostDto post) {
        Duration duration = Duration.ofHours(newsFeedProperties.getCountHoursTimeToLive());
        redisCacheService.put(key, post, duration);
    }
}
