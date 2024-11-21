package faang.school.postservice.repository;

import faang.school.postservice.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CacheViewRepository implements CacheRepository<Long> {

    private final CacheService<Long> cacheService;

    @Override
    public void save(String viewKey, Long viewAuthorId) {
        viewKey += "::count_post_view";
        cacheService.incrementAndGet(viewKey);
    }
}
