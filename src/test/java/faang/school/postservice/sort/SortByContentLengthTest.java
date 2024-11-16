package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortByContentLengthTest {
    private SortByContentLength sortByContentLength = new SortByContentLength();

    @Test
    void testGetComparator() {
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();

        post1.setContent("Large content");
        post2.setContent("medi content");
        post3.setContent("sm content");

        List<Post> posts = Arrays.asList(post1, post2, post3);

        posts.sort(sortByContentLength.getComparator());

        assertEquals(post3, posts.get(0));
        assertEquals(post2, posts.get(1));
        assertEquals(post1, posts.get(2));
    }
}