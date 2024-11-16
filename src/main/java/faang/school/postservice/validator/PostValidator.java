package faang.school.postservice.validator;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {
    private PostRepository postRepository;

    public void checkPostExistence(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post does not exist");
        }
    }
}
