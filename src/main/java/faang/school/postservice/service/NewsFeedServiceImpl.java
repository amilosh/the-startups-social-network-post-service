package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.news.feed.NewsFeed;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.AsyncCacheService;
import faang.school.postservice.service.cache.MultiGetCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class NewsFeedServiceImpl implements NewsFeedService {

    private final PostService postService;
    private final CommentService commentService;
    private final SingleCacheService<Long, UserDto> userCacheService;
    private final SingleCacheService<Long, Long> viewCacheService;
    private final ExecutorService newsFeedThreadPoolExecutor;
    private final UserServiceClient userServiceClient;
    private final MultiGetCacheService<Long, LikeDto> likeCacheService;
    private final PostRepository postRepository;
    private final NewsFeedProperties newsFeedProperties;
    private final AsyncCacheService<Long, Long> newsFeedAsyncCacheService;

    @Override
    public NewsFeed getNewsFeedBy(long userId) {
        return getNewsFeedBy(userId, -1L);
    }

    @Override
    public NewsFeed getNewsFeedBy(long userId, long firstPostId) {
        List<Long> postIds = getPostIdsForNewsFeedBy(userId, firstPostId);
        List<PostDto> posts = postService.getPosts(postIds);

        CompletableFuture<?>[] futures = posts
                .stream()
                .map(this::preparePost)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();

        return new NewsFeed(posts);
    }

    @Override
    public List<Long> getPostIdsForNewsFeedBy(long userId, long firstPostId) {
        int newsFeedSize = newsFeedProperties.getNewsFeedSize();
        List<Long> postIds = newsFeedAsyncCacheService.getRange(userId, firstPostId, newsFeedSize).join();

        if (postIds.isEmpty()) {
            List<Long> followerIds = userServiceClient.getFollowingIds(userId);
            List<Long> postIdsForFullNewsFeed = postRepository.getIdsForNewsFeed(followerIds, newsFeedSize);
            int lastIndexBatchSize = newsFeedProperties.getBatchSize() - 1;

            List<Long> postIdsForCache = postIdsForFullNewsFeed.subList(lastIndexBatchSize, postIdsForFullNewsFeed.size() - 1);
            asyncSavePostsToCache(userId, postIdsForCache);

            return postIdsForFullNewsFeed.subList(0, lastIndexBatchSize);
        } else {
            return postIds;
        }
    }

    private void asyncSavePostsToCache(long userId, List<Long> postIdsForCache) {
        newsFeedThreadPoolExecutor.execute(() ->
                postIdsForCache.forEach(postId -> newsFeedAsyncCacheService.save(userId, postId))
        );
    }

    private CompletableFuture<Void> preparePost(PostDto post) {
        return CompletableFuture.runAsync(() -> {
            Long postId = post.getId();

            UserDto postAuthor = userCacheService.get(post.getAuthorId());
            post.setAuthor(postAuthor);

            List<LikeDto> likes = likeCacheService.getAll(postId);
            post.setLikes(likes);

            List<CommentDto> comments = commentService.getCommentsByPostId(postId, newsFeedProperties.getLimitCommentsOnPost());
            post.setComments(comments);

            comments.forEach(comment -> {
                UserDto commentAuthor = userCacheService.get(comment.getAuthorId());
                comment.setAuthor(commentAuthor);
            });

            Long viewsCount = viewCacheService.get(postId);
            post.setViewsCount(viewsCount);

        }, newsFeedThreadPoolExecutor);
    }
}
