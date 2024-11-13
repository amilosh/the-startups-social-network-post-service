package faang.school.postservice.filter.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostFilter {
    boolean isApplicable(FilterDto filterDto);

    void apply(List<Post> posts, FilterDto filterDto);
}
