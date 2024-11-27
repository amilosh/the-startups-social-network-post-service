package faang.school.postservice.service.album.album_filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumUpdatedAtFilterTest {

    private AlbumFilterDto albumFilterDto;
    private AlbumUpdatedAtFilter albumUpdatedAtFilter;
    private Stream<Album> albumStream;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .updatedAt(LocalDateTime.of(2024,10,10,4,5))
                .build();
        albumUpdatedAtFilter = new AlbumUpdatedAtFilter();
        albumStream = Stream.of(
                Album.builder().updatedAt(LocalDateTime.of(2024,10,10,4,5)).build(),
                Album.builder().updatedAt(LocalDateTime.of(2024,10,10,4,5)).build(),
                Album.builder().updatedAt(LocalDateTime.of(2024,11,10,4,5)).build());
    }

    @Test
    public void testApply() {
        List<Album> albums = albumUpdatedAtFilter
                .apply(albumStream, albumFilterDto)
                .stream()
                .toList();
        assertEquals(2, albums.size());
        albums.forEach(album ->
                assertEquals(album.getUpdatedAt(), albumFilterDto.getUpdatedAt()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(albumUpdatedAtFilter.isApplicable(albumFilterDto));
        assertFalse(albumUpdatedAtFilter.isApplicable(new AlbumFilterDto()));
    }
}
