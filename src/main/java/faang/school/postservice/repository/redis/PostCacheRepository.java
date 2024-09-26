package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<Post, Long> {

}