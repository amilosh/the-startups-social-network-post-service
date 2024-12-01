package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.redis.cache.PostCacheDto;

import java.util.Optional;

public interface PostCacheService {
    void savePost(PostCacheDto post);

    Optional<PostCacheDto> getPost(Long id);

    void addLike(Long postId, Long likeId);

    void addComment(Long postId, Long commentId);
}
