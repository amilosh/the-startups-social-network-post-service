package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment getComment(long id) {
        return commentRepository.findById(id).orElseThrow(
                () ->new EntityNotFoundException(String.format("Comment %s not found", id)));
    }

    public boolean existsCommentById(Long id) {
        if (id == null) return false;
        return commentRepository.existsById(id);
    }
}
