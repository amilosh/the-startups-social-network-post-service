package faang.school.postservice.validator;

import faang.school.postservice.exception.UnauthorizedAccessException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;

    public void validateCommentExistsById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment with id " + id + " doesn't exist");
        }
    }

    public void validateCommentAuthorId(Comment comment, long authorId) {
        if (comment.getAuthorId() != authorId) {
            throw new UnauthorizedAccessException(
                    String.format("Comment with id %d doesn't belong to the user with id %d", comment.getId(), authorId));
        }
    }
}
