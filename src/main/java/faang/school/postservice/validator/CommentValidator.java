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

    public void validateComment(CommentDtoInput commentDtoInput) {
        if (commentDtoInput.getContent() == null || commentDtoInput.getContent().isEmpty()) {
            throw new DataValidationException("Comment content is empty");
        }
        if (commentDtoInput.getContent().length() > 4096) {
            throw new DataValidationException("Comment content is too long");
        }
        if (commentDtoInput.getAuthorId() == null) {
            throw new DataValidationException("Comment must have an author");
        }
        if (commentDtoInput.getPostId() == null) {
            throw new DataValidationException("Comment must relate to a post");
        }
    }

    public void validateCommentUpdateDto(CommentUpdateDto updatingDto){
        Comment comment = commentRepository.findById(updatingDto.getCommentId()).orElse(null);
        if(comment == null){
            throw new DataValidationException("Comment with id " + updatingDto.getCommentId() + " does not exist");
        }
        if (updatingDto.getContent() == null || updatingDto.getContent().isEmpty()) {
            throw new DataValidationException("Comment content is empty");
        }
        if (updatingDto.getContent().length() > 4096) {
            throw new DataValidationException("Comment content is too long");
        }
    }

    public void validatePostExists(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new DataValidationException("Post with id " + postId + " does not exist");
        }
    }

    public void validateAuthorExists(CommentDtoInput commentDto) {
        long authorId = commentDto.getAuthorId();
        try {
            UserDto userDto = userServiceClient.getUser(authorId);
            if (userDto == null) {
                throw new IllegalArgumentException("User with id " + authorId + " does not exist");
            }
        } catch (Exception e) {
            log.error("Error validating user existence: {}", e.getMessage());
            throw new DataValidationException("User with id " + authorId + " does not exist");
        }
    }
}