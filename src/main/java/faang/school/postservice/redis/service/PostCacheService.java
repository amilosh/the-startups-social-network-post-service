package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.redis.model.entity.PostCache;

public interface PostCacheService {

    void savePostToCache(PostDto post);
    PostCache updatePostComments(Long id);
}
