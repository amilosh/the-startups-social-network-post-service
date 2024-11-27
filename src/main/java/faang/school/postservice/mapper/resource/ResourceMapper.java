package faang.school.postservice.mapper.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    ResourceDto toDto(Resource resource);

    List<ResourceDto> toDto(List<Resource> resources);

    Resource ToEntity(ResourceDto resourceDto);

    List<Resource> ToEntity(List<ResourceDto> resourceDtos);
}
