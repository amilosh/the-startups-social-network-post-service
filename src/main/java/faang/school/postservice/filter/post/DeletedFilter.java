package faang.school.postservice.filter.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeletedFilter implements PostFilter{
    @Override
    public boolean isApplicable(FilterDto filterDto) {
        return filterDto.getDeleted() != null;
    }

    @Override
    public void apply(List<Post> posts, FilterDto filterDto) {
        posts.removeIf(post -> post.isDeleted() != filterDto.getDeleted());
    }
}
