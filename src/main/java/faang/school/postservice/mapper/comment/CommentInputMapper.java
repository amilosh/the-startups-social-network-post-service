package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentInputMapper {
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentDtoInput toDto(Comment comment);

    @Mapping(source = "likeIds", target = "likes", qualifiedByName = "mapLikeIdsToLikes")
    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDtoInput commentDto);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "post.id", target = "postId")
    List<CommentDtoInput> toDto(List<Comment> comments);

    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikeIdsToLikes")
    @Mapping(source = "post.id", target = "postId")
    List<Comment> toEntity(List<CommentDtoInput> commentDtos);

    @Named("mapLikeIdsToLikes")
    default List<Like> mapLikeIdsToLikes(List<Long> likeIds) {
        if (likeIds == null) {
            return null;
        }
        return likeIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setId(id);
                    return like;
                })
                .collect(Collectors.toList());
    }

    @Named("mapLikesToLikeIds")
    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }
}