package faang.school.postservice.validator.post.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.enums.PostType;
import faang.school.postservice.service.post.filter.AuthorPostFilter;
import faang.school.postservice.validator.post.PostValidator;
import org.hibernate.validator.internal.util.Contracts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthorPostFilterTest {

    private PostValidator postValidator;

    private AuthorPostFilter authorPostFilter;
    @BeforeEach
    public void setUp(){
        authorPostFilter = new AuthorPostFilter(postValidator);
    }

    @Test
    public  void shouldFilterPostsByAuthorId() {
        // Arrange
        PostValidator postValidator = mock(PostValidator.class);
        AuthorPostFilter authorPostFilter = new AuthorPostFilter(postValidator);

        Long authorId = 1L;
        PostFilterDto filterDto = new PostFilterDto();
        filterDto.setAuthorId(authorId);

        Post post1 = Post.builder()
                .authorId(1L)
                .build();
        Post post2 = Post.builder()
                .authorId(2L)
                .build();

        Stream<Post> posts = Stream.of(post1, post2);
        Stream<Post> result = authorPostFilter.apply(posts, filterDto);
        List<Post> filteredPosts = result.collect(Collectors.toList());
        assertEquals(1, filteredPosts.size());
        assertEquals(authorId, filteredPosts.get(0).getAuthorId());

    }


    @Test
    public void shouldReturnTrueWhenPostTypeIsAuthor() {
        AuthorPostFilter authorPostFilter1 = new AuthorPostFilter(null);
        PostFilterDto filterDto = new PostFilterDto();
        filterDto.setType(PostType.AUTHOR);
        boolean result = authorPostFilter1.isApplicable(filterDto);
        Contracts.assertTrue(result, "The method should return true when the post type is PROJECT.");
    }
}
