package faang.school.postservice.service;

import faang.school.postservice.cache.PostCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.FeedPost;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.FeedPostMapper;
import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.NewsFeedRedisRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsFeedService {

    private final NewsFeedRedisRepository newsFeedRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final PostCacheMapper postCacheMapper;
    private final FeedPostMapper feedPostMapper;

    @Value("${spring.data.redis.cache.news-feed.batch-size}")
    private int batchSize;

    public void allocateToFeeds(Long postId, Long createdAt, List<Long> userIds) {
        userIds.forEach(userId -> newsFeedRedisRepository.addPostId(postId, userId, createdAt));
        log.info("Post allocated to feeds. PostId: {}", postId);
    }

    @Transactional(readOnly = true)
    public List<FeedPost> getFeedBatch(Long userId, Long lastPostId) {
        List<Long> cachedPostIds;
        if (Objects.isNull(lastPostId)) {
            cachedPostIds = newsFeedRedisRepository.getPostIdsFirstBatch(userId);
        } else {
            cachedPostIds = newsFeedRedisRepository.getPostIdsBatch(userId, lastPostId);
        }
        List<PostCache> postCaches = new ArrayList<>(findPostCaches(cachedPostIds));
        if (postCaches.size() < batchSize) {
            List<PostCache> additionalPostCaches = findPersistedPostsForUser(userId, cachedPostIds, batchSize - postCaches.size());
            postCaches.addAll(additionalPostCaches);
        }
        return feedPostMapper.toFeedPostsList(postCaches);
    }

    public void startHeat() {
        int page = 0;
        while (true) {
            List<UserDto> users = userServiceClient.getUsersWithFollowings(page++, 2);
            if (users.isEmpty()) {
                break;
            }
            System.out.println(users);
        }
    }



    private List<PostCache> findPostCaches(List<Long> postIds) {
        return postIds.stream()
                .map(postRedisRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private List<PostCache> findPersistedPostsForUser(Long userId, List<Long> postIds, int limit) {
        UserDto user = userServiceClient.getUser(userId);
        List<Long> followingIds = user.getFollowingsIds();
        Long lastPostCachedId = postIds.isEmpty() ? null : postIds.get(postIds.size() - 1);

        List<Post> persistedPosts = postRepository.findFeedPost(followingIds, lastPostCachedId, limit);
        return persistedPosts.stream()
                .map(postCacheMapper::toPostCache)
                .toList();
    }


}