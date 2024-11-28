package faang.school.postservice.repository.cache.post;

import faang.school.postservice.dto.cache.post.PostCacheDto;

import java.util.Optional;

public interface PostCacheRepository {

    void save(PostCacheDto postCacheDto);

    void incrementLikesCount(Long postId);

    Optional<PostCacheDto> findById(Long postId);
}
