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

public class AlbumCreatedAtFilterTest {

    private AlbumFilterDto albumFilterDto;
    private AlbumCreatedAtFilter albumCreatedAtFilter;
    private Stream<Album> albumStream;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .createdAt(LocalDateTime.of(2024,10,10,4,5))
                .build();
        albumCreatedAtFilter = new AlbumCreatedAtFilter();
        albumStream = Stream.of(
                Album.builder().createdAt(LocalDateTime.of(2024,10,10,4,5)).build(),
                Album.builder().createdAt(LocalDateTime.of(2024,10,10,4,5)).build(),
                Album.builder().createdAt(LocalDateTime.of(2024,11,10,4,5)).build());
    }

    @Test
    public void testApply() {
        List<Album> albums = albumCreatedAtFilter
                .apply(albumStream, albumFilterDto)
                .stream()
                .toList();
        assertEquals(2, albums.size());
        albums.forEach(album ->
                assertEquals(album.getCreatedAt(), albumFilterDto.getCreatedAt()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(albumCreatedAtFilter.isApplicable(albumFilterDto));
        assertFalse(albumCreatedAtFilter.isApplicable(new AlbumFilterDto()));
    }
}
