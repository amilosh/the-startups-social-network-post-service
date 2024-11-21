package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.PostDto;

public interface PostCacheService {

    void savePostToCache(PostDto post);
}
