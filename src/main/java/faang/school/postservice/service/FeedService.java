package faang.school.postservice.service;

public interface FeedService {
    void bindPostToFollower(Long followerId, Long postId);
}
