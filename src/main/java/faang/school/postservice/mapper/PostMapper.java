package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.response.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDto toDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums", ignore = true)
    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "scheduledAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post toEntity(CreatePostDto createPostDto);
}
