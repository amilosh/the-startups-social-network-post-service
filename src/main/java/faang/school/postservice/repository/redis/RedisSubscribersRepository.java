package faang.school.postservice.repository.redis;

import faang.school.postservice.model.entity.redis.SubscribersCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisSubscribersRepository extends CrudRepository<SubscribersCache, Long> {
}
