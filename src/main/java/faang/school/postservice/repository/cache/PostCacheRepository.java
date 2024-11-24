package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.redis.cache.PostCacheDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCacheDto, Long> {
}
