package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.news_feed.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.NewsFeedRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    @Value("${spring.data.redis.cache.feed.batch-size}")
    private int feedBatchSize;

    public void allocateToFeeds(Long postId, Long createdAt, List<Long> userIds) {
        userIds.forEach(followerId -> newsFeedRepository.addPost(postId, followerId, createdAt));
    }

    @Transactional(readOnly = true)
    public List<FeedPostDto> getPostBatch() {
        Long userId = userContext.getUserId();
        List<Long> batch = newsFeedRepository.getPostBatch(userId);
        if (batch.size() < feedBatchSize) {
            UserDto userDto = userServiceClient.getUser(userId);
            List<Long> followingsIds = userDto.getFollowingsIds();
            int limit = feedBatchSize - batch.size();
            Long lastPostId = batch.get(batch.size() - 1);
            List<Post> persistPost = postRepository.getPersistentPostBatch(followingsIds, lastPostId, limit);


        }

        return null;
    }
}
