package faang.school.postservice.repository.redis;

import faang.school.postservice.model.entity.redis.AuthorCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<AuthorCache, Long> {
}
