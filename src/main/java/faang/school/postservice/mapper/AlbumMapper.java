package faang.school.postservice.mapper;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    Album toEntity(AlbumCreateDto createDto);

    @Mapping(source = "posts", target = "postIds")
    AlbumDto toDto(Album album);

    default Long postToPostId(Post post) {
        return post.getId();
    }
}
