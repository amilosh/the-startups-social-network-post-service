package faang.school.postservice.service.post.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;

import java.util.stream.Stream;

public interface PostFilters {

    boolean isApplicable(PostFilterDto postFilterDto);
    Stream<Post> apply(Stream<Post> posts, PostFilterDto filterDto);
}
