package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CommentMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostMapper {

    @Mapping(target = "likeCount", source = "likeCount")
    PostResponseDto toResponseDto(Post post, int likeCount);

    @Mapping(target = "likeCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    PostResponseDto toDto(Post post);

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "likesCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    PostCacheDto toCacheDto(Post post);
}
