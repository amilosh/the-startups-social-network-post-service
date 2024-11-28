package faang.school.postservice.mapper;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "post.id", target = "postId")
    ResourceDto toDto(Resource resource);

    @Mapping(source = "postId", target = "post.id")
    Resource toEntity(ResourceDto resourceDto);
}