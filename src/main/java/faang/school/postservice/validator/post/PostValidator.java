package faang.school.postservice.validator.post;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final PostRepository postRepository;

    public Post validatePostExistence(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id %d wasn't found", postId)));
    }
}
