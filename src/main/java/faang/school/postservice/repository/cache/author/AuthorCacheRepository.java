package faang.school.postservice.repository.cache.author;

import faang.school.postservice.dto.cache.author.EventAuthorDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<EventAuthorDto, Long> {

}
