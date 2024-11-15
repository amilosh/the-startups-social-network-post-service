package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumTitleFilter  implements Filter<Album, AlbumFilterDto> {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitle() != null;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter){
        return albums.filter(album -> album.getTitle().equals(filter.getTitle()));
    }
}
