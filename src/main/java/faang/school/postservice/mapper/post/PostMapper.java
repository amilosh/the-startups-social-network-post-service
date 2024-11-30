package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "countLikes", qualifiedByName = "toCountIds")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapCommentIds")
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikeIds")
    PostResponseDto toDto(Post post);

    Post toEntity(PostRequestDto postRequestDto);

    List<PostResponseDto> toDtoList(List<Post> posts);

    @Named("toCountIds")
    default Long toCountIds(List<Like> likes) {
        return (long) (likes == null ? 0 : likes.size());
    }

    @Named("mapCommentIds")
    default List<Long> mapCommentIds(List<Comment> comments) {
        if (comments == null) return Collections.emptyList();
        return comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
    }

    @Named("mapLikeIds")
    default List<Long> mapLikeIds(List<Like> likes) {
        if (likes == null) return Collections.emptyList();
        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }
}
