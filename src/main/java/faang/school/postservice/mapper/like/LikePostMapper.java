package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikePostMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(target = "numberOfLikes", expression = "java(countLikes(like))")
    LikePostDto toDto(Like like);

    @Mapping(source = "postId", target = "post", ignore = true)
    Like toEntity(LikePostDto likePostDto);

    default Long countLikes(Like like) {
        return like.getPost() != null && like.getPost().getLikes() != null
                ? (long) like.getPost().getLikes().size() + 1
                : 0L;
    }
}
