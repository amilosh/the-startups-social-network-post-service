package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likeCount", source = "likeCount")
    PostResponseDto toResponseDto(Post post, int likeCount);

    PostResponseDto toDto(Post post);
}
