package faang.school.postservice.repository.ad;

import faang.school.postservice.cache.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<PostCache, Long> {

}
