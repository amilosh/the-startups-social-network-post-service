package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumTitleFilterTest extends SetUpFilterTest {
    private AlbumTitleFilter albumTitleFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        albumTitleFilter = new AlbumTitleFilter();
    }

    @Test
    public void testAlbumTitleFilterisApplicable() {
        assertTrue(albumTitleFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumTitleFilterNotApplicable() {
        albumFilterDto.setTitle(null);

        assertFalse(albumTitleFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumTitleFilter() {
        List<Album> filteredAlbums = albumTitleFilter.apply(albums, albumFilterDto).toList();

        assertEquals(1, filteredAlbums.size());
        assertEquals("Filter", filteredAlbums.get(0).getTitle());

    }
}
