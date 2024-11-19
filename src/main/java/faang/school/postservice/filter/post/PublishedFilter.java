package faang.school.postservice.filter.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PublishedFilter implements PostFilter {
    @Override
    public boolean isApplicable(FilterDto filterDto) {
        return filterDto.getPublished() != null;
    }

    @Override
    public void apply(List<Post> posts, FilterDto filterDto) {
        posts.removeIf(post -> post.isPublished() != filterDto.getPublished());
    }
}
