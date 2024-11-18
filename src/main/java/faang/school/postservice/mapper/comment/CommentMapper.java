package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentNotificationEvent;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {
    Comment toEntity(CommentRequestDto commentRequestDto);

    Collection<CommentResponseDto> toDtos(Collection<Comment> comments);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "listOfLikesToIds")
    @Mapping(target = "postId", source = "post.id")
    CommentResponseDto toDto(Comment comment);

    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "authorId", source = "savedComment.authorId")
    @Mapping(target = "commentId", source = "savedComment.id")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    CommentEvent toCommentEvent(Long postId, Comment savedComment);

    @Mapping(target = "postId", source = "postId")
    @Mapping(target = "commentId", source = "savedComment.id")
    @Mapping(target = "authorPostId", source = "postAuthorId")
    @Mapping(target = "authorCommentId", source = "savedComment.authorId")
    @Mapping(target = "content", source = "savedComment.content")
    CommentNotificationEvent toNotificationEvent(Long postId, Comment savedComment, Long postAuthorId);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "mapLikes")
    @Mapping(source = "post.id", target = "postId")
    CommentCacheDto toCommentCacheDto(Comment comment);

    @Named("listOfLikesToIds")
    @BeanMapping
    default Collection<Long> listOfLikesToIds(Collection<Like> likes) {
        if (likes != null) {
            return likes.stream().map(Like::getId).toList();
        }
        return new ArrayList<>();
    }

    @Named("mapLikes")
    default Long mapLikes(List<Like> likes) {
        if (likes == null) return 0L;
        return (long) likes.size();
    }
}
