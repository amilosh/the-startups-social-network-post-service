package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.like.LikeListMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = LikeListMapper.class)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likeIds", qualifiedByName = "mapToLikeId")
    CommentDto toDto(Comment comment);

    @Mapping(target = "post", expression = "java(mapPostId(commentDto.getPostId()))")
    Comment toEntity(CommentDto commentDto);

    @Named("mapToLikeId")
    default List<Long> mapToLikeId(List<Like> likes) {
        return likes.stream()
                .map(Like::getId)
                .toList();
    }

    default Post mapPostId(Long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }
}
