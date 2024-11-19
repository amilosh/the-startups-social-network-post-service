package faang.school.postservice.filter.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DeletedFilterTest {
    private DeletedFilter deletedFilter = new DeletedFilter();
    private FilterDto filterDto = new FilterDto();

    @Test
    void testNullDeleted() {
        assertFalse(deletedFilter.isApplicable(filterDto));
    }

    @Test
    void testSuccessfulDeleted() {
        filterDto.setDeleted(true);
        assertTrue(deletedFilter.isApplicable(filterDto));
    }

    @Test
    void testSuccessfulApply() {
        filterDto.setDeleted(true);

        Post firstPost = new Post();
        Post secondPost = new Post();

        firstPost.setDeleted(false);
        secondPost.setDeleted(true);

        List<Post> posts = new ArrayList<>();
        Stream.of(firstPost,secondPost).forEach(posts::add);

        deletedFilter.apply(posts, filterDto);

        assertEquals(posts.size(), 1);
        assertEquals(posts.get(0), secondPost);
    }
}