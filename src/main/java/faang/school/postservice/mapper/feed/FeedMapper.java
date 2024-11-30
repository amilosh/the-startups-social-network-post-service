package faang.school.postservice.mapper.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.CommentFeedResponseDto;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.dto.feed.UserFeedResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.comment.CommentLikes;
import faang.school.postservice.model.comment.CommentRedis;
import faang.school.postservice.model.post.PostLikes;
import faang.school.postservice.model.post.PostRedis;
import faang.school.postservice.model.post.PostViews;
import faang.school.postservice.repository.CommentLikesRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostLikesRepository;
import faang.school.postservice.repository.PostViewsRepository;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeedMapper {
    private final static String POST_LIKES_REDIS_KEY = "postLikes:";
    private final static String POST_VIEWS_REDIS_KEY = "postViews:";
    private final static String COMMENT_LIKES_REDIS_KEY = "commentLikes:";

    private final UserRedisRepository userRedisRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRedisRepository commentRedisRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> commonRedisTemplate;
    private final PostLikesRepository postLikesRepository;
    private final PostViewsRepository postViewsRepository;
    private final CommentLikesRepository commentLikesRepository;

    public List<PostFeedResponseDto> mapToCommentFeedResponseDto(List<PostRedis> posts) {
        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }

        return posts.stream()
                .map(this::mapToPostFeedResponseDto)
                .toList();
    }

    public PostFeedResponseDto mapToPostFeedResponseDto(PostRedis post) {
        Integer likes = mapToPostLikes(post.getId());
        Integer views = mapToPostViews(post.getId());
        UserFeedResponseDto author = mapToUserFeedResponseDto(post.getAuthorId());
        List<CommentFeedResponseDto> feedComments = mapToCommentFeedResponseDtos(post.getComments());

        return buildPostFeedResponseDto(post, likes, views, author, feedComments);
    }

    private Integer mapToPostLikes(Long postId) {
        String key = buildPostLikesKey(postId);
        Object value = commonRedisTemplate.opsForHash().get(key, "likes");
        if (value != null) {
            return Integer.parseInt(value.toString());
        } else {
            return postLikesRepository.findById(postId)
                    .map(PostLikes::getAmount)
                    .orElseGet(() -> {
                        log.error("PostLikes {} not found", postId);
                        return 0;
                    });
        }
    }

    private Integer mapToPostViews(Long postId) {
        String key = buildPostViewsKey(postId);
        Object value = commonRedisTemplate.opsForHash().get(key, "views");
        if (value != null) {
            return Integer.parseInt(value.toString());
        } else {
            return postViewsRepository.findByPostId(postId)
                    .map(PostViews::getAmount)
                    .orElseGet(() -> {
                        log.error("PostViews {} not found", postId);
                        return 0;
                    });
        }
    }

    private UserFeedResponseDto mapToUserFeedResponseDto(Long authorId) {
        return userRedisRepository.findById(authorId)
                .map(userRedis -> new UserFeedResponseDto(userRedis.getId(), userRedis.getUsername()))
                .orElseGet(() -> {
                    UserDto userDto = userServiceClient.getUser(authorId);
                    return new UserFeedResponseDto(userDto.getId(), userDto.getUsername());
                });
    }

    private List<CommentFeedResponseDto> mapToCommentFeedResponseDtos(List<Long> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }

        return comments.stream()
                .map(this::mapToCommentFeedResponseDto)
                .toList();
    }

    private CommentFeedResponseDto mapToCommentFeedResponseDto(Long commentId) {
        return commentRedisRepository.findById(commentId)
                .map(this::mapToCommentFeedResponseDto)
                .orElseGet(() -> commentRepository.findById(commentId)
                        .map(this::buildCommentFeedResponseDto)
                        .orElse(null)
                );
    }

    private CommentFeedResponseDto mapToCommentFeedResponseDto(CommentRedis commentRedis) {
        Integer likes = mapToCommentLikes(commentRedis.getId());
        return buildCommentFeedResponseDto(commentRedis, likes);
    }

    private Integer mapToCommentLikes(Long commentId) {
        String key = buildCommentLikesKey(commentId);
        Object value = commonRedisTemplate.opsForHash().get(key, "likes");
        if (value != null) {
            return Integer.parseInt(value.toString());
        } else {
            return commentLikesRepository.findByCommentId(commentId)
                    .map(CommentLikes::getAmount)
                    .orElseGet(() -> {
                        log.error("PostLikes {} not found", commentId);
                        return 0;
                    });
        }
    }

    private CommentFeedResponseDto buildCommentFeedResponseDto(CommentRedis commentRedis, Integer likes) {
        return CommentFeedResponseDto.builder()
                .id(commentRedis.getId())
                .content(commentRedis.getContent())
                .likes(likes)
                .authorId(commentRedis.getAuthorId())
                .build();
    }

    private CommentFeedResponseDto buildCommentFeedResponseDto(Comment comment) {
        return CommentFeedResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .likes(comment.getLikes().size())
                .authorId(comment.getAuthorId())
                .build();
    }

    private PostFeedResponseDto buildPostFeedResponseDto(PostRedis post, Integer likes, Integer views,
                                                         UserFeedResponseDto author, List<CommentFeedResponseDto> feedComments) {
        return PostFeedResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .likes(likes)
                .views(views)
                .author(author)
                .comments(feedComments)
                .build();
    }

    private String buildPostLikesKey(Long postId) {
        return POST_LIKES_REDIS_KEY + postId;
    }

    private String buildPostViewsKey(Long postId) {
        return POST_VIEWS_REDIS_KEY + postId;
    }

    private String buildCommentLikesKey(Long postId) {
        return COMMENT_LIKES_REDIS_KEY + postId;
    }
}
