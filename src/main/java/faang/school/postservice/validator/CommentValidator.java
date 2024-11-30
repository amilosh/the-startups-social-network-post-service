package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;

    public final static String AUTHOR_ID_IS_NULL = "AuthorId is null in Comment";
    public final static String POST_ID_IS_NULL = "PostId is null in Comment";
    public final static String CONTENT_IS_EMPTY = "Comment content is empty";
    public final static String COMMENT_NOT_EXIST_BY_ID = "Comment with id = %s does not exist";
    public final static String COMMENT_NOT_FOUND = "Comment with id = %s not found";

    public final static String POST_NOT_FOUND = "Post with id = %s not found";

    public void validateComment(CommentDto commentDto) {
        validateAuthorId(commentDto.getAuthorId());
        validatePostId(commentDto.getPostId());
    }

    public void validateContent(String content) {
        if (content == null || content.isBlank())
            throw new IllegalArgumentException(CONTENT_IS_EMPTY);
    }

    private void validateAuthorId(Long authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException(AUTHOR_ID_IS_NULL);
        }
    }

    private void validatePostId(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException(POST_ID_IS_NULL);
        }
    }

    public void validateCommentExist(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            String error = String.format(COMMENT_NOT_EXIST_BY_ID, commentId);
            throw new IllegalArgumentException(error);
        }
    }
}
