package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "likes", target = "likeIds")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "likeIds", target = "likes")
    @Mapping(source = "postId", target = "post")
    Comment toEntity(CommentDto commentDto);

    default List<Long> mapLikesToIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    default List<Like> mapIdsToLikes(List<Long> likeIds) {
        if (likeIds == null) {
            return new ArrayList<>();
        }
        return likeIds.stream()
                .map(id -> {
                    Like like = new Like();
                    like.setId(id);
                    return like;
                })
                .toList();
    }

    default Post mapIdToPost(Long id) {
        if (id == null) {
            return null;
        }

        Post post = new Post();
        post.setId(id);
        return post;
    }
}
