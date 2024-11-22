package faang.school.postservice.redis.service;

public interface AuthorCacheService {

    void saveAuthorToCache(Long postAuthorId);
}
