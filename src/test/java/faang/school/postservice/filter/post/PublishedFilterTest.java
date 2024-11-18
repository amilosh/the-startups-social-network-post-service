package faang.school.postservice.filter.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PublishedFilterTest {
    private PublishedFilter publishedFilter = new PublishedFilter();
    private FilterDto filterDto = new FilterDto();

    @Test
    void testNullDeleted() {
        assertFalse(publishedFilter.isApplicable(filterDto));
    }

    @Test
    void testSuccessfulDeleted() {
        filterDto.setPublished(true);
        assertTrue(publishedFilter.isApplicable(filterDto));
    }

    @Test
    void testSuccessfulApply() {
        filterDto.setPublished(true);

        Post firstPost = new Post();
        Post secondPost = new Post();

        firstPost.setPublished(false);
        secondPost.setPublished(true);

        List<Post> posts = new ArrayList<>();
        Stream.of(firstPost,secondPost).forEach(posts::add);

        publishedFilter.apply(posts, filterDto);

        assertEquals(posts.size(), 1);
        assertEquals(posts.get(0), secondPost);
    }
}