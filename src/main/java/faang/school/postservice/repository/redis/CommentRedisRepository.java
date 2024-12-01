package faang.school.postservice.repository.redis;

import faang.school.postservice.model.comment.CommentRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRedisRepository extends CrudRepository<CommentRedis, Long> {
}
