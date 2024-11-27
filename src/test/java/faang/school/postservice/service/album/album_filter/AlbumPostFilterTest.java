package faang.school.postservice.service.album.album_filter;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumPostFilterTest {

    private AlbumFilterDto albumFilterDto;
    private AlbumPostFilter albumPostFilter;
    private Stream<Album> albumStream;

    @BeforeEach
    public void setUp() {
        Post firstPost = Post.builder()
                .id(1L)
                .build();
        Post secondPost = Post.builder()
                .id(2L)
                .build();
        Post thirdPost = Post.builder()
                .id(3L)
                .build();
        albumFilterDto = AlbumFilterDto.builder()
                .posts(List.of(1L))
                .build();
        albumPostFilter = new AlbumPostFilter();
        albumStream = Stream.of(
                Album.builder().posts(List.of(firstPost)).build(),
                Album.builder().posts(List.of(firstPost, secondPost)).build(),
                Album.builder().posts(List.of(thirdPost)).build());
    }

    @Test
    public void testApply() {
        List<Album> albums = albumPostFilter
                .apply(albumStream, albumFilterDto)
                .stream()
                .toList();
        assertEquals(2, albums.size());
    }

    @Test
    public void testIsApplicable() {
        assertTrue(albumPostFilter.isApplicable(albumFilterDto));
        assertFalse(albumPostFilter.isApplicable(new AlbumFilterDto()));
    }

}
