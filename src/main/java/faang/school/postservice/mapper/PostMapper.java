package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "comments", target = "commentIds")
    PostDto toDto(Post post);

    @Mapping(source = "likeIds", target = "likes")
    @Mapping(source = "commentIds", target = "comments")
    Post toEntity(PostDto postDto);

    default List<Long> mapLikesToIds(List<Like> likes) {
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    default List<Like> mapIdsToLikes(List<Long> likeIds) {
        return likeIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setId(id);
                    return like;
                })
                .toList();
    }

    default List<Long> mapCommentsToIds(List<Comment> likes) {
        return likes.stream()
                .map(Comment::getId)
                .toList();
    }

    default List<Comment> mapIdsToComments(List<Long> commentIds) {
        return commentIds.stream()
                .map(id -> {
                    Comment comment = new Comment();
                    comment.setId(id);
                    return comment;
                })
                .toList();
    }
}
