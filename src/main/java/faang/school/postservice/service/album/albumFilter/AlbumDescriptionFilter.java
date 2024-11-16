package faang.school.postservice.service.album.albumFilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;
import java.util.stream.Stream;

public class AlbumDescriptionFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public List<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
        return albums.filter(album -> album.getDescription().contains(filters.getDescriptionPattern()))
                .toList();
    }

}
