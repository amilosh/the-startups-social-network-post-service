package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepositoryI extends CrudRepository<PostRedis, Long> {
}
