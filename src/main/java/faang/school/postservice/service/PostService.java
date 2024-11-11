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

/*   когда будешь получать посты используй
     findByProjectIdWithLikes(long projectId)
     findByAuthorIdWithLikes(long userId)*/

    public Post getPost(long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post %s not found", id)));
    }

    public boolean existsPostById(Long id) {
        if (id == null) return false;
        return postRepository.existsById(id);
    }
}
