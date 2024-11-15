package faang.school.postservice.filter.albumfilter;

import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlbumDescriptionFilterTest extends SetUpFilterTest {
    private AlbumDescriptionFilter albumDescriptionFilter;

    @BeforeEach
    void setUp() {
        super.setUp();
        albumDescriptionFilter = new AlbumDescriptionFilter();
    }

    @Test
    public void testAlbumDescriptionFilterIsApplicable() {
        assertTrue(albumDescriptionFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumDescriptionFilterNotApplicable() {
        albumFilterDto.setDescription(null);
        assertFalse(albumDescriptionFilter.isApplicable(albumFilterDto));
    }

    @Test
    public void testAlbumDescriptionFilter() {
        List <Album>  filteredAlbums = albumDescriptionFilter.apply(albums,albumFilterDto).toList();

        assertEquals(2,filteredAlbums.size());
        assertEquals("Java the best",filteredAlbums.get(0).getDescription());
        assertEquals("Java the best",filteredAlbums.get(1).getDescription());
    }
}
