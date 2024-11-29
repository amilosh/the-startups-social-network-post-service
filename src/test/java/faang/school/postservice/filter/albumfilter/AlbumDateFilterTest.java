package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumDateFilterTest extends SetUpFilterTest {
    private AlbumDateFilter albumDateFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        albumDateFilter = new AlbumDateFilter();
    }

    @Test
    public void testAlbumDateFilterIsApplicable() {
        assertTrue(albumDateFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumDateFilterNotApplicable() {
        albumFilterDto.setMonth(null);
        assertFalse(albumDateFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumDateFilter() {
        List<Album> filteredAlbums = albumDateFilter.apply(albums, albumFilterDto).toList();

        assertEquals(1, filteredAlbums.size());
        assertEquals(Month.MARCH, filteredAlbums.get(0).getCreatedAt().getMonth());
    }
}
