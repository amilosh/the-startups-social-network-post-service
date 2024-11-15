package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.dto.comment.CommentUpdateDto;
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

    public void validateCommentDtoInput(CommentDtoInput commentDtoInput) {
        if (commentDtoInput.getContent() == null || commentDtoInput.getContent().isEmpty()) {
            log.error("Data validation Exception - comment content is empty");
            throw new DataValidationException("Comment content is empty");
        }
        if (commentDtoInput.getContent().length() > 4096) {
            log.error("Data validation Exception - comment content too long");
            throw new DataValidationException("Comment content is too long");
        }
        if (commentDtoInput.getAuthorId() == null) {
            log.error("Data validation Exception - comment author id is null");
            throw new DataValidationException("Comment must have an author");
        }
        if (commentDtoInput.getPostId() == null) {
            log.error("Data validation Exception - comment post id is null");
            throw new DataValidationException("Comment must relate to a post");
        }
    }

    public void validateCommentUpdateDto(CommentUpdateDto commentUpdateDto){
        Long commentId = commentUpdateDto.getCommentId();
        if(commentId == null){
            log.error("Data validation Exception - comment id is null");
            throw new DataValidationException("Comment must have an id");
        }
        if (commentUpdateDto.getContent() == null || commentUpdateDto.getContent().isEmpty()) {
            log.error("Data validation Exception - comment content is empty on update");
            throw new DataValidationException("Comment content is empty");
        }
        if (commentUpdateDto.getContent().length() > 4096) {
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

    public void validateAuthorExists(CommentDtoInput commentDto) {
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
}