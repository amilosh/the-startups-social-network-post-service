package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "post.id", target = "postId")
    ResponseCommentDto toDto(Comment comment);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "post.id", target = "postId")
    List<ResponseCommentDto> toDto(List<Comment> comments);

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(RequestCommentDto commentDto);

    @Mapping(source = "postId", target = "post.id")
    List<Comment> toEntity(List<ResponseCommentDto> commentDtos);

    @Named("mapLikesToLikeIds")
    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }
}