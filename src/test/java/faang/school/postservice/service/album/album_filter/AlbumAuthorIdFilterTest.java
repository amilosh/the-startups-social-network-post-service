package faang.school.postservice.service.album.album_filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AlbumAuthorIdFilterTest {

    private AlbumFilterDto albumFilterDto;
    private AlbumAuthorIdFilter albumAuthorIdFilter;
    private Stream<Album> albumStream;

    @BeforeEach
    public void setUp() {
        albumFilterDto = faang.school.postservice.dto.album.AlbumFilterDto.builder()
                .authorId(34L)
                .build();
        albumAuthorIdFilter = new AlbumAuthorIdFilter();
        albumStream = Stream.of(
                Album.builder().authorId(34L).build(),
                Album.builder().authorId(34L).build(),
                Album.builder().authorId(35L).build());
    }

    @Test
    public void testApply() {
        List<Album> albums = albumAuthorIdFilter
                .apply(albumStream, albumFilterDto)
                .stream()
                .toList();
        assertEquals(2, albums.size());
        albums.forEach(album ->
                        assertEquals(34L, album.getAuthorId()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(albumAuthorIdFilter.isApplicable(albumFilterDto));
        assertFalse(albumAuthorIdFilter.isApplicable(new AlbumFilterDto()));
    }
}
