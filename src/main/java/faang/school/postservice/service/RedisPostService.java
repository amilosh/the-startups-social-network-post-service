package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;

public interface RedisPostService {
    void savePostIfNotExists(RedisPostDto postDto);

    RedisPostDto getPost(Long postId);

    void addComment(CommentSentKafkaEvent event);

    void incrementLikesWithTransaction(Long postId, Long likeId);

    void savePost(RedisPostDto postDto);

    void incrementPostViewsWithTransaction(Long postId, Long viewerId, String viewDateTime);
}
