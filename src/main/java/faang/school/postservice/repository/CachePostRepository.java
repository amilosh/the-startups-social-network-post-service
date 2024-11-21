package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CachePostRepository implements CacheRepository<PostDto> {

    private final CacheService<PostDto> cacheService;

    @Value("${server.cache.post.count-hours-time-to-live}")
    private int timeToLivePost;

    @Override
    public void save(String key, PostDto post) {
        key += "::post";
        Duration timeToLive = Duration.ofHours(timeToLivePost);
        cacheService.set(key, post, timeToLive);
    }
}
