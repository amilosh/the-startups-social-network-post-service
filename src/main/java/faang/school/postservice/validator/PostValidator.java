package faang.school.postservice.validator;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exceptions.EntityNotFoundException;
import faang.school.postservice.repository.PostRepository;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {
    private PostRepository postRepository;

    public void checkPostExistence(PostDto postDto) {
        if (!postRepository.existsById(postDto.getId())) {
            throw new EntityNotFoundException("Post does not exist");
        }
    }
}
