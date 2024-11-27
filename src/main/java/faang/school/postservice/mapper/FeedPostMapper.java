package faang.school.postservice.mapper;

import faang.school.postservice.cache.CommentCache;
import faang.school.postservice.cache.PostCache;
import faang.school.postservice.cache.UserCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.FeedComment;
import faang.school.postservice.dto.post.FeedPost;
import faang.school.postservice.dto.user.FeedUser;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeedPostMapper {
    private final UserRedisRepository userRedisRepository;
    private final UserServiceClient userServiceClient;
    private final UserCacheMapper userCacheMapper;

    public List<FeedPost> toFeedPostsList(List<PostCache> postCaches) {
        return postCaches.stream()
                .map(this::toFeedPost)
                .toList();
    }

    private FeedPost toFeedPost(PostCache postCache) {
        return FeedPost.builder()
                .postId(postCache.getId())
                .author(findUserCache(postCache.getAuthorId()))
                .publishedAt(postCache.getPublishedAt())
                .resourceKeys(postCache.getResourceKeys())
                .viewCount(postCache.getViewCount())
                .likeCount(postCache.getLikeCount())
                .commentsCount(postCache.getCommentsCount())
                .comments(toFeedCommentList(postCache.getComments()))
                .build();
    }

    private List<FeedComment> toFeedCommentList(List<CommentCache> commentCaches) {
        return commentCaches.stream()
                .map(this::toFeedComment)
                .toList();
    }

    private FeedComment toFeedComment(CommentCache commentCache) {
        return FeedComment.builder()
                .commentId(commentCache.getId())
                .author(findUserCache(commentCache.getAuthorId()))
                .content(commentCache.getContent())
                .createdAt(commentCache.getCreatedAt())
                .build();
    }

    private FeedUser findUserCache(Long userId) {
        Optional<UserCache> userCache = userRedisRepository.findById(userId);
        if (userCache.isPresent()) {
            return toFeedUser(userCache.get());
        } else {
            UserDto userDto = userServiceClient.getUser(userId);
            return toFeedUser(userCacheMapper.toUserCache(userDto));
        }
    }

    private FeedUser toFeedUser(UserCache userCache) {
        return FeedUser.builder()
                .userId(userCache.getId())
                .username(userCache.getUsername())
                .smallAvatar(userCache.getAvatarSmall())
                .build();
    }
}
