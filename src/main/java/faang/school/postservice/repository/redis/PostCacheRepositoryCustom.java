package faang.school.postservice.repository.redis;

public interface PostCacheRepositoryCustom {
    void incrementLikesCount(Long postId);
}
