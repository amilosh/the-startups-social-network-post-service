package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.cache.feed.FeedCacheDto;

import java.util.Optional;

public interface FeedCacheRepository {

    void save(FeedCacheDto feedCacheDto);

    Optional<FeedCacheDto> findBySubscriberId(Long subscriberId);

    void addPostId(FeedCacheDto feedCacheDto, Long postId);
}
