package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class AlbumDateFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filterDto) {
        LocalDateTime createdBefore = filterDto.getCreatedBefore();
        LocalDateTime createdAfter = filterDto.getCreatedAfter();
        return (createdAfter != null && createdBefore == null) || (createdAfter == null && createdBefore != null);
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filterDto) {
        return filterDto.getCreatedAfter() != null
                ? albums.filter(album -> album.getCreatedAt().isAfter(filterDto.getCreatedAfter()))
                : albums.filter(album -> album.getCreatedAt().isAfter(filterDto.getCreatedBefore()));
    }
}