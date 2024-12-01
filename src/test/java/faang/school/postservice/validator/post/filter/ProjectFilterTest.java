package faang.school.postservice.validator.post.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.enums.PostType;
import faang.school.postservice.service.post.filter.ProjectPostFilter;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectFilterTest {
    private PostValidator postValidator;

    private ProjectPostFilter projectPostFilter;

    @BeforeEach
    public void setUp() {
        projectPostFilter = new ProjectPostFilter(postValidator);
    }

    @Test
    public void shouldFilterPostsByProjectId() {
        // Arrange
        PostValidator postValidator = mock(PostValidator.class);
        ProjectPostFilter projectPostFilter = new ProjectPostFilter(postValidator);

        Long projectId = 1L;
        PostFilterDto filterDto = new PostFilterDto();
        filterDto.setProjectId(projectId);

        Post post1 = Post.builder()
                .projectId(1L)
                .build();
        Post post2 = Post.builder()
                .projectId(2L)
                .build();

        Stream<Post> posts = Stream.of(post1, post2);

        Stream<Post> result = projectPostFilter.apply(posts, filterDto);

        List<Post> filteredPosts = result.collect(Collectors.toList());
        assertEquals(1, filteredPosts.size());
        assertEquals(projectId, filteredPosts.get(0).getProjectId());
    }

    @Test
    public void shouldReturnTrueWhenPostTypeIsProject() {
        ProjectPostFilter projectPostFilter = new ProjectPostFilter(null);
        PostFilterDto filterDto = new PostFilterDto();
        filterDto.setType(PostType.PROJECT);

        boolean result = projectPostFilter.isApplicable(filterDto);
        assertTrue(result, "The method should return true when the post type is PROJECT.");
    }
}
