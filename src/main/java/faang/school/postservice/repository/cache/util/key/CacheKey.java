package faang.school.postservice.repository.cache.util.key;

import org.springframework.stereotype.Component;

@Component
public class CacheKey {
    // PostCacheRepository
//    @Value("${app.post.cache.news_feed.prefix.post_id}")
//    private String postIdPrefix;
//
//    @Value("${app.post.cache.news_feed.postfix.views}")
//    private String viewsPostfix;
//
//    @Value("${app.post.cache.news_feed.postfix.likes}")
//    private String likesPostfix;
//
//    @Value("${app.post.cache.news_feed.postfix.comments}")
//    private String commentsPostfix;

    // CommentCacheRepository
//    @Value("${app.post.cache.news_feed.prefix.comment_id}")
//    private String commentIdPrefix;
//
//    @Value("${app.post.cache.news_feed.prefix.post_comments_ids_set}")
//    private String commentsIdsSetPrefix;

    // UserCacheRepository
//    @Value("${app.post.cache.news_feed.prefix.user_id}")
//    private String userIdPrefix;

//    // FeedService
//    @Value("${app.post.cache.news_feed.prefix.feed_user_id}")
//    private String feedUserIdPrefix;

    // Общий метод для построения ключей
    public String buildKey(String prefix, long id, String postfix) {
        return prefix + id + (postfix != null ? postfix : "");
    }

    // Методы для PostCache
//    public String buildPostViewsKey(long postId) {
//        return buildKey(postIdPrefix, postId, viewsPostfix);
//    }
//
//    public String buildPostLikesKey(long postId) {
//        return buildKey(postIdPrefix, postId, likesPostfix);
//    }
//
//    public String buildPostCommentsKey(long postId) {
//        return buildKey(postIdPrefix, postId, commentsPostfix);
//    }
//
//    public String getPostIdFromKey(String key) {
//        return key.split("/")[0];
//    }

    // Методы для CommentCache
//    public String buildCommentKey(long commentId) {
//        return buildKey(commentIdPrefix, commentId, null);
//    }
//
//    public String buildCommentLikesKey(long commentId) {
//        return buildKey(commentIdPrefix, commentId, likesPostfix);
//    }
//
//    public String buildPostCommentsSetKey(long postId) {
//        return buildKey(commentsIdsSetPrefix + postIdPrefix, postId, null);
//    }
//
//    public String getCommentIdFromKey(String key) {
//        return key.split("/")[0];
//    }

    // Методы для UserCache
//    public String buildUserKey(long userId) {
//        return buildKey(userIdPrefix, userId, null);
//    }

    // Методы для FeedService
//    public String buildFeedUserKey(long userId) {
//        return buildKey(feedUserIdPrefix, userId, null);
//    }
//
//    public String buildPostKey(long postId) {
//        return buildKey(postIdPrefix, postId, null);
//    }








////    PostCacheRepository
//
//    @Value("${app.post.cache.news_feed.prefix.post_id}")
//    private String postIdPrefix;
//
//    @Value("${app.post.cache.news_feed.postfix.views}")
//    private String viewsPostfix;
//
//    @Value("${app.post.cache.news_feed.postfix.likes}")
//    private String likesPostfix;
//
//    @Value("${app.post.cache.news_feed.postfix.comments}")
//    private String commentsPostfix;
//
//    public String postViewId(long id) {
//        return buildId(id) + viewsPostfix;
//    }
//
//    public String postLikeIdBuild(long id) {
//        return buildId(id) + likesPostfix;
//    }
//
//    public String commentsCounterKeyBuild(long id) {
//        return buildId(id) + commentsPostfix;
//    }
//
//    public String buildId(long id) {
//        return postIdPrefix + id;
//    }
//
//    public String getPostId(String viewKey) {
//        return viewKey.split("/")[0];
//    }
//
//    // CommentCacheRepository
//
//    @Value("${app.post.cache.news_feed.prefix.post_id}")
//    private String postIdPrefix;
//
//    @Value("${app.post.cache.news_feed.prefix.comment_id}")
//    private String commentIdPrefix;
//
//    @Value("${app.post.cache.news_feed.prefix.post_comments_ids_set}")
//    private String commentsIdsSetPrefix;
//
//    @Value("${app.post.cache.news_feed.postfix.likes}")
//    private String likesPostfix;
//
//    public String commentsIdsSetKeyBuild(long postId) {
//        return commentsIdsSetPrefix + postIdPrefix + postId;
//    }
//
//    public String commentLikesCounterKeyBuild(long id) {
//        return commentKeyBuild(id) + likesPostfix;
//    }
//
//    public String commentKeyBuild(long id) {
//        return commentIdPrefix + id;
//    }
//
//    public String getCommentIdKey(String viewKey) {
//        return viewKey.split("/")[0];
//    }
//
//    // UserCacheRepository
//
//    @Value("${app.post.cache.news_feed.prefix.user_id}")
//    private String userIdPrefix;
//
//    public String buildId(long id) {
//        return userIdPrefix + id;
//    }
//
//    // FeedService
//
//    @Value("${app.post.cache.news_feed.prefix.feed_user_id}")
//    private String feedUserIdPrefix;
//
//    @Value("${app.post.cache.news_feed.prefix.post_id}")
//    private String postIdPrefix;
//
//    @Value("${app.post.cache.news_feed.prefix.user_id}")
//    private String userIdPrefix;
//
//    public String feedUserKey(long id) {
//        return feedUserIdPrefix + id;
//    }
//
//    public String postIdKey(long id) {
//        return postIdPrefix + id;
//    }
//
//    public String userIdKey(long id) {
//        return userIdPrefix + id;
//    }
}
