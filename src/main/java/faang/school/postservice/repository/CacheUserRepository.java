package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CacheUserRepository implements CacheRepository<UserDto> {

    private final CacheService<UserDto> cacheService;

    @Value("${server.cache.user.count-hours-time-to-live}")
    private int timeToLiveCommentAuthor;

    @Override
    public void save(String key, UserDto user) {
        key += "::user";
        Duration timeToLive = Duration.ofHours(timeToLiveCommentAuthor);
        cacheService.set(key, user, timeToLive);
    }
}
