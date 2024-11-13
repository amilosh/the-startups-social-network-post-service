package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CommentMapper.class, LikeMapper.class})
public interface PostMapper {

    Post toEntity(PostDto postDto);

    @Mapping(source = "ad.id", target = "adId")
    @Mapping(source = "resources", target = "resourcesIds", qualifiedByName = "toLong")
    PostDto toDto(Post post);

    @Named("toLong")
    default List<Long> toLong(List<Resource> resources) {
        List<Long> resourcesIds = new ArrayList<>();
        if (resources != null) {
            resourcesIds = resources.stream()
                    .map(Resource::getId)
                    .toList();
        }
        return resourcesIds;
    }
}
