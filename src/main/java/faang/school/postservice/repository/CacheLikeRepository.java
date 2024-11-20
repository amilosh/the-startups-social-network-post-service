package faang.school.postservice.repository;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.cache.ListCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CacheLikeRepository implements CacheRepository<LikeDto> {

    private final ListCacheService<LikeDto> listCacheService;

    @Override
    public void save(String postId, LikeDto likeDto) {
        String likesKeyOnPost = postId + "::post::likes";
        listCacheService.rightPush(likesKeyOnPost, likeDto);
    }
}
