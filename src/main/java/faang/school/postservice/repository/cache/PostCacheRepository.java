package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.PostCacheDto;

public interface PostCacheRepository {

    void save(PostCacheDto postCacheDto);
}
