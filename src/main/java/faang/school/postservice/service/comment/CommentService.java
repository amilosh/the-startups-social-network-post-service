package faang.school.postservice.service.comment;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment getEntityComment(Long commentId) {
        return getComment(commentId);
    }

    public void addLikeToComment(Long commentId, Like like) {
        Comment comment = getComment(commentId);
        comment.getLikes().add(like);

        log.info("Adding like to comment with ID: {}", comment.getId());
        commentRepository.save(comment);
    }

    public void removeLikeFromComment(Long commentId, Like like) {
        Comment comment = getComment(commentId);
        comment.getLikes().remove(like);

        log.info("Removing like from comment with ID: {}", comment.getId());
        commentRepository.save(comment);
    }

    private Comment getComment(Long commentId) {
        log.info("Try to get comment by id: {}", commentId);

        return commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Comment with id %s not found", commentId)));
    }
}
