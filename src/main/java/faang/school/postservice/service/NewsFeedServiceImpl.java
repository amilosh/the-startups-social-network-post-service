package faang.school.postservice.service;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.news.feed.NewsFeed;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
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
    private final MultiGetCacheService<Long, LikeDto> likeCacheService;
    private final SortedSetCacheRepository<Long> newsFeedSortedSetCacheRepository;
    private final NewsFeedProperties newsFeedProperties;
    private final ExecutorService newsFeedThreadPoolExecutor;

    @Override
    public NewsFeed getNewsFeedBy(long userId) {
        return getNewsFeedBy(userId, -1L);
    }

    @Override
    public NewsFeed getNewsFeedBy(long userId, long firstPostId) {
        List<Long> postIds = newsFeedSortedSetCacheRepository.getRange(
                userId + "::news_feed",
                Long.toString(firstPostId),
                0,
                newsFeedProperties.getBatchSize(),
                Long.class
        );
        List<PostDto> posts = postService.getPosts(postIds);

        CompletableFuture<?>[] futures = posts
                .stream()
                .map(this::preparePost)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();

        return new NewsFeed(posts);
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
