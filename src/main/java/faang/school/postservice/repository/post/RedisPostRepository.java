package faang.school.postservice.repository.post;

import faang.school.postservice.model.redis.PostRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostRedis, Long> {
}