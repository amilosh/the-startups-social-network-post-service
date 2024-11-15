package faang.school.postservice.validator;

import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final PostRepository postRepository;

    public void validatePostExistsById(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id: %s doesn't exist", postId));
        }
    }
}
