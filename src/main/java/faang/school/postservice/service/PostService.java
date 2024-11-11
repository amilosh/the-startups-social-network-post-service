package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id #%s not found", id)));
    }
}
