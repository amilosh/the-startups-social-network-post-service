package faang.school.postservice.service.post.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.enums.PostType;
import faang.school.postservice.validator.post.PostValidator;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public class ProjectPostFilter implements PostFilters {
    private final PostValidator postValidator;


    @Override
    public Stream<Post> apply(Stream<Post> posts, PostFilterDto filterDto) {
        return posts.filter(post -> post.getProjectId().equals(filterDto.getProjectId()));

    }

    @Override
    public boolean isApplicable(PostFilterDto postFilterDto) {
        return postFilterDto.getType() != null && PostType.PROJECT == postFilterDto.getType();
    }
}
