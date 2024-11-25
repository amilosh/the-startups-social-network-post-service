package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.feed.CommentFeedResponseDto;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.dto.feed.UserFeedResponseDto;
import faang.school.postservice.dto.redis.CommentRedisEntity;
import faang.school.postservice.dto.redis.PostRedisEntity;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FeedMapper {
    private final UserRedisRepository userRedisRepository;
    private final CommentRedisRepository commentRedisRepository;

    public List<PostFeedResponseDto> map(List<PostRedisEntity> post) {
        return post.stream().map(this::build).toList();
    }

    public PostFeedResponseDto build(PostRedisEntity post) {
        UserRedis userRedis = userRedisRepository.findById(post.getAuthorId()).orElse(null);
        UserFeedResponseDto author = new UserFeedResponseDto(userRedis.getId(), userRedis.getUsername());
        List<Long> comments = post.getComments();
        List<CommentFeedResponseDto> feedComments = mapToCommentList(comments);

        return PostFeedResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .likes(post.getLikes())
                .views(post.getViews())
                .author(author)
                .comments(feedComments)
                .build();
    }

    private List<CommentFeedResponseDto> mapToCommentList(List<Long> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments.stream().map(this::map).toList();
    }

    private CommentFeedResponseDto map(Long id) {
        CommentRedisEntity commentRedisEntity = commentRedisRepository.findById(id).orElse(null);
        return CommentFeedResponseDto.builder()
                .id(commentRedisEntity.getId())
                .content(commentRedisEntity.getContent())
                .likes(commentRedisEntity.getLikes())
                .authorId(commentRedisEntity.getAuthorId())
                .build();
    }
}
