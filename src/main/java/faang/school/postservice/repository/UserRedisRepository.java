package faang.school.postservice.repository;

import faang.school.postservice.cache.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedisRepository extends CrudRepository<UserCache, Long> {

}
