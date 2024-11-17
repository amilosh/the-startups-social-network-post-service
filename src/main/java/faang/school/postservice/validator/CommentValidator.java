package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.RequestCommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void validateCommentDtoInput(RequestCommentDto requestCommentDto) {
        if (requestCommentDto.getContent() == null || requestCommentDto.getContent().isEmpty()) {
            log.error("Data validation Exception - comment content is empty");
            throw new DataValidationException("Comment content is empty");
        }
        if (requestCommentDto.getContent().length() > 4096) {
            log.error("Data validation Exception - comment content too long");
            throw new DataValidationException("Comment content is too long");
        }
        if (requestCommentDto.getAuthorId() == null) {
            log.error("Data validation Exception - comment author id is null");
            throw new DataValidationException("Comment must have an author");
        }
        if (requestCommentDto.getPostId() == null) {
            log.error("Data validation Exception - comment post id is null");
            throw new DataValidationException("Comment must relate to a post");
        }
    }

    public void validateCommentUpdateDto(RequestCommentUpdateDto requestCommentUpdateDto){
        Long commentId = requestCommentUpdateDto.getCommentId();
        if(commentId == null){
            log.error("Data validation Exception - comment id is null");
            throw new DataValidationException("Comment must have an id");
        }
        if (requestCommentUpdateDto.getContent() == null || requestCommentUpdateDto.getContent().isEmpty()) {
            log.error("Data validation Exception - comment content is empty on update");
            throw new DataValidationException("Comment content is empty");
        }
        if (requestCommentUpdateDto.getContent().length() > 4096) {
            log.error("Data validation Exception - comment content too long on update");
            throw new DataValidationException("Comment content is too long");
        }
    }

    public void validateCommentExists(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            log.error("Data validation Exception - comment does not exist");
            throw new EntityNotFoundException("Comment with id " + commentId + " does not exist");
        }
    }

    public void validatePostExists(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            log.error("Data validation Exception - post does not exist");
            throw new EntityNotFoundException("Post with id " + postId + " does not exist");
        }
    }

    public void validateAuthorExists(RequestCommentDto commentDto) {
        long authorId = commentDto.getAuthorId();
        try {
            UserDto userDto = userServiceClient.getUser(authorId);
            if (userDto == null) {
                log.error("Data validation Exception - author does not exist");
                throw new EntityNotFoundException("User with id " + authorId + " does not exist");
            }
        } catch (Exception e) {
            log.error("Error validating user existence: {}", e.getMessage());
            throw new DataValidationException("User with id " + authorId + " does not exist");
        }
    }

    public void validateCommentIdIsNullForCreatingNewComment(RequestCommentDto commentDto) {
        Long id = commentDto.getId();
        if(id != null) {
            log.error("Data validation Exception - id must be null to create new comment");
            throw new DataValidationException("Comment id must be null to create a new comment");
        }
    }
}