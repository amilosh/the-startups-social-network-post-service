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

public class AlbumDescriptionFilterTest {

    private AlbumFilterDto albumFilterDto;
    private AlbumDescriptionFilter albumDescriptionFilter;
    private Stream<Album> albumStream;

    @BeforeEach
    public void setUp() {
        albumFilterDto = AlbumFilterDto.builder()
                .descriptionPattern("описание")
                .build();
        albumDescriptionFilter = new AlbumDescriptionFilter();
        albumStream = Stream.of(
                Album.builder().description("описание 1").build(),
                Album.builder().description("описание 2").build(),
                Album.builder().description("ЗА КОРОЛЯ!!!").build());
    }

    @Test
    public void testApply() {
        List<Album> albums = albumDescriptionFilter
                .apply(albumStream, albumFilterDto)
                .stream()
                .toList();
        assertEquals(2, albums.size());
        albums.forEach(album ->
                assertTrue(album.getDescription().contains(albumFilterDto.getDescriptionPattern())));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(albumDescriptionFilter.isApplicable(albumFilterDto));
        assertFalse(albumDescriptionFilter.isApplicable(new AlbumFilterDto()));
    }
}
