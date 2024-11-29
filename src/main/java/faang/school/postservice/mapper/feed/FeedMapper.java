package faang.school.postservice.mapper.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.CommentFeedResponseDto;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.dto.feed.UserFeedResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.comment.CommentRedis;
import faang.school.postservice.model.post.PostLikes;
import faang.school.postservice.model.post.PostRedis;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostLikesRepository;
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

    private final UserRedisRepository userRedisRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRedisRepository commentRedisRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> commonRedisTemplate;
    private final PostLikesRepository postLikesRepository;

    public List<PostFeedResponseDto> mapToCommentFeedResponseDto(List<PostRedis> posts) {
        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }

        return posts.stream()
                .map(this::mapToPostFeedResponseDto)
                .toList();
    }

    public PostFeedResponseDto mapToPostFeedResponseDto(PostRedis post) {
        Integer likes = mapToLikes(post.getId());
        UserFeedResponseDto author = mapToUserFeedResponseDto(post.getAuthorId());
        List<CommentFeedResponseDto> feedComments = mapToCommentFeedResponseDtos(post.getComments());

        return buildPostFeedResponseDto(post, likes, author, feedComments);
    }

    private Integer mapToLikes(Long postId) {
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
                .map(this::buildCommentFeedResponseDto)
                .orElseGet(() -> commentRepository.findById(commentId)
                        .map(this::buildCommentFeedResponseDto)
                        .orElse(null)
                );
    }

    private CommentFeedResponseDto buildCommentFeedResponseDto(CommentRedis commentRedis) {
        return CommentFeedResponseDto.builder()
                .id(commentRedis.getId())
                .content(commentRedis.getContent())
                .likes(commentRedis.getLikes())
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

    private PostFeedResponseDto buildPostFeedResponseDto(PostRedis post, Integer likes, UserFeedResponseDto author, List<CommentFeedResponseDto> feedComments) {
        return PostFeedResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .likes(likes)
                .views(post.getViews())
                .author(author)
                .comments(feedComments)
                .build();
    }

    private String buildPostLikesKey(Long postId) {
        return POST_LIKES_REDIS_KEY + postId;
    }
}