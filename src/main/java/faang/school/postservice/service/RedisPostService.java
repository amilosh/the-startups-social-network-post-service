package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;

public interface RedisPostService {
    void savePostIfNotExists(RedisPostDto postDto);

    RedisPostDto getPost(Long postId);

    void addComment(Long postId, String comment);

    void incrementLikesWithTransaction(Long postId);

    void savePost(RedisPostDto postDto);
}
