package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoOutputUponUpdate;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentOutputUponUpdateMapper {
    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentDtoOutputUponUpdate toDto(Comment comment);

    @Mapping(source = "likeIds", target = "likes")
    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDtoOutputUponUpdate commentDto);

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "post.id", target = "postId")
    List<CommentDtoOutputUponUpdate> toDto(List<Comment> comments);

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "post.id", target = "postId")
    List<Comment> toEntity(List<CommentDtoOutputUponUpdate> commentDtos);


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

    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }
}
