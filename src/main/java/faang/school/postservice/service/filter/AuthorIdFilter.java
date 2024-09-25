package faang.school.postservice.service.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class AuthorIdFilter implements PostFilter{

    @Override
    public boolean isApplicable(PostFilterDto postFilterDto) {
        return postFilterDto.getAuthorId() != null;
    }

    @Override
    public Stream<Post> apply(Stream<Post> posts, PostFilterDto postFilterDto) {
        return posts.filter(post -> Objects.equals(post.getAuthorId(), postFilterDto.getAuthorId()));
    }
}