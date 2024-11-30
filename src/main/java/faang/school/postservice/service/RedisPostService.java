package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.CommentSentEvent;

public interface RedisPostService {
    void savePostIfNotExists(RedisPostDto postDto);

    RedisPostDto getPost(Long postId);

    void addComment(CommentSentEvent event);

    void incrementLikesWithTransaction(Long postId);

    void savePost(RedisPostDto postDto);

    void incrementPostViewsWithTransaction(Long postId);
}
