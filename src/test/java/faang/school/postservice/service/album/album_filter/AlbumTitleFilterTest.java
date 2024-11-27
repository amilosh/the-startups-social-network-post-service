package faang.school.postservice.service.album.album_filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumTitleFilterTest {

    private AlbumFilterDto albumFilterDto;
    private AlbumTitleFilter albumTitleFilter;
    private Stream<Album> albumStream;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .titlePattern("Заголовок")
                .build();
        albumTitleFilter = new AlbumTitleFilter();
        albumStream = Stream.of(
                Album.builder().title("Заголовок 1").build(),
                Album.builder().title("Заголовок 2").build(),
                Album.builder().title("Что-то другое").build());
    }

    @Test
    public void testApply() {
        List<Album> albums = albumTitleFilter
                .apply(albumStream, albumFilterDto)
                .stream()
                .toList();
        assertEquals(2, albums.size());
        albums.forEach(album ->
                assertTrue(album.getTitle().contains(albumFilterDto.getTitlePattern())));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(albumTitleFilter.isApplicable(albumFilterDto));
        assertFalse(albumTitleFilter.isApplicable(new AlbumFilterDto()));
    }
}
