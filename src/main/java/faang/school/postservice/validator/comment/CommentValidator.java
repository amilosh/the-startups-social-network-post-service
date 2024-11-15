package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    public Comment getExistingComment(Long commentId) {
        log.info("Searching for comment with ID: {}", commentId);
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment with ID: {} not found", commentId);
            return new EntityNotFoundException("Comment with id: " + commentId + " does not exist");
        });
    }

    public void isAuthorExist(Long authorId) {
        log.info("Checking if author exists with ID: {}", authorId);
        if (userServiceClient.getUser(authorId) == null) {
            log.error("Author with ID: {} does not exist", authorId);
            throw new EntityNotFoundException("Author with ID " + authorId + " does not exist");
        }
    }

    public void isPostExist(Long postId) {
        log.info("Checking if post exists with ID: {}", postId);
        if (postRepository.findById(postId).isEmpty()) {
            log.error("Post with ID: {} does not exist", postId);
            throw new EntityNotFoundException("Post with ID " + postId + " does not exist");
        }
    }

}
