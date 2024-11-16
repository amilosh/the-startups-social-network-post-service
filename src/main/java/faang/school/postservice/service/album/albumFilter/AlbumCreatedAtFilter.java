package faang.school.postservice.service.album.albumFilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;
import java.util.stream.Stream;

public class AlbumCreatedAtFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getCreatedAt() != null;
    }

    @Override
    public List<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
        return albums.filter(album -> album.getCreatedAt() == filters.getCreatedAt())
                .toList();
    }
}
