package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment findEntityById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Comment with id '%s' not found", id)));
    }
}
