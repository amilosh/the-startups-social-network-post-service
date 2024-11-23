package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {
    @Mapping(target = "posts", ignore = true)
    Album toEntity(AlbumDto eventDto);

    @Mapping(source = "posts", target = "postIds", qualifiedByName = "map")
    AlbumDto toDto(Album event);

    @Named("map")
    default List<Long> map(List<Post> posts) {
        return posts.stream().map(Post::getId).toList();
    }
}
