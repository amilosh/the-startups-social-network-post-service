package faang.school.postservice.service.album.albumFilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;

import java.util.List;
import java.util.stream.Stream;

public class AlbumPostFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getPosts() != null;
    }

    @Override
    public List<Album> apply(Stream<Album> albums, AlbumFilterDto filters) {
        return albums.filter(album -> album.getPosts()
                        .stream().map(Post::getAd)
                        .anyMatch(id -> filters.getPosts().contains(id)))
                .toList();
    }

}
