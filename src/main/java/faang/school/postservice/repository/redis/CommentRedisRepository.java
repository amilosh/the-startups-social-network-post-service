package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.CommentRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRedisRepository extends CrudRepository<CommentRedisEntity, Long> {
}
