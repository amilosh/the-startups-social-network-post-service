package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.model.post.Post;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@RequiredArgsConstructor
public abstract class CacheablePostMapper {
    @Value("${feed.cache.post.time-to-live-in-seconds}")
    private long timeToLive;

    private final CommentMapper commentMapper;

    @Mapping(target = "countOfLikes", source = "likes.size()")
    @Mapping(target = "countOfComments", source = "comments.size()")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "commentsMap")
    @Mapping(target = "timeToLive", qualifiedByName = "timeToLive")
    abstract CacheablePost toCacheablePost(Post post);

    @Named("commentsMap")
    public List<CommentDto> commentsMap(List<Comment> comments) {
        return comments.stream()
                .limit(3L)
                .map(commentMapper::toDto)
                .toList();
    }

    @Named("timeToLive")
    public long getTimeToLive() {
        return timeToLive;
    }
}
