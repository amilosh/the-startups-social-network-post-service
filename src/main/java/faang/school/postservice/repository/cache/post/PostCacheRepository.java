package faang.school.postservice.repository.cache.post;

import faang.school.postservice.dto.cache.post.PostCacheDto;

public interface PostCacheRepository {

    void save(PostCacheDto postCacheDto);
}
