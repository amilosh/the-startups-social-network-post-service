package faang.school.postservice.service.album.albumFilter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.List;
import java.util.stream.Stream;

public interface AlbumFilter {

    boolean isApplicable(AlbumFilterDto filters);

    List<Album> apply(Stream<Album> albumStream, AlbumFilterDto filters);
}
