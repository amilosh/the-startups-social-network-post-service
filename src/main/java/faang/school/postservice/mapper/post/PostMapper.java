package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ResourceMapper.class)
@DecoratedWith(PostMapperDecorator.class)
public interface PostMapper {

    @Mapping(target = "images", ignore = true)
    @Mapping(target = "audio", ignore = true)
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikesToLikeIds")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapCommentsToCommentIds")
    @Mapping(source = "likes", target = "countLikes", qualifiedByName = "toCountIds")
    @Mapping(source = "comments", target = "commentIds", qualifiedByName = "mapCommentIds")
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapLikeIds")
    PostResponseDto toDto(Post post);

    Post toEntity(PostRequestDto postDto);

    @Named("mapLikesToLikeIds")
    default List<Long> mapLikesToLikeIds(List<Like> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    @Named("mapCommentsToCommentIds")
    default List<Long> mapCommentsToCommentIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
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
}