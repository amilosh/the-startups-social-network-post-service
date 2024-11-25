package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostCacheDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCacheDto, Long>, PostCacheRepositoryCustom {

}
