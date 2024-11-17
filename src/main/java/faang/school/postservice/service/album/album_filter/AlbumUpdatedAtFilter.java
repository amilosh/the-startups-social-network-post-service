package faang.school.postservice.service.album.album_filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;
import java.util.stream.Stream;

public class AlbumUpdatedAtFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getUpdatedAt() != null;
    }

    @Override
    public List<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
        return albums.filter(album -> album.getUpdatedAt().isEqual(filters.getUpdatedAt()))
                .toList();
    }

}
