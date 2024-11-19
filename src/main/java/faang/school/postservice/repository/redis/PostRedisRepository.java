package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.PostRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<PostRedisEntity, Long> {
}
