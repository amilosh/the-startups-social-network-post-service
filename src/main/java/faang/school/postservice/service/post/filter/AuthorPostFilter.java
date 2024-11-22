package faang.school.postservice.service.post.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.post.PostValidator;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public class AuthorPostFilter implements PostFilters {
    private final PostValidator postValidator;

    @Override
    public void apply(Stream<Post> posts, PostFilterDto filterDto) {
        postValidator.validateUserExist(filterDto.getId());
        posts = posts.filter(post -> post.getAuthorId().equals(filterDto.getId()));
    }

    @Override
    public boolean isApplicable(PostFilterDto postFilterDto) {
        return "author".equalsIgnoreCase(postFilterDto.getType());
    }
}
