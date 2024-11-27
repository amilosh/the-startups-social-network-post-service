package faang.school.postservice.service.post.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.post.PostValidator;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;
@AllArgsConstructor
public class ProjectPostFilter implements PostFilters{
    private final PostValidator postValidator;

    @Override
    public boolean isApplicable(PostFilterDto postFilterDto) {
        return "project".equalsIgnoreCase(postFilterDto.getType());
    }

    @Override
    public Stream<Post> apply(Stream<Post> posts, PostFilterDto filterDto) {
        postValidator.validateProjectExist(filterDto.getId());
        return posts.filter(post -> post.getProjectId().equals(filterDto.getId()));

    }
}
