package faang.school.postservice.validator.comment;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;

    public Comment validateCommentExistence(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with ID: %d wasn't found", commentId)));
    }
}
