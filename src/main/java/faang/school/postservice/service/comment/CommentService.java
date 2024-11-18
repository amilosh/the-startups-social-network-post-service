package faang.school.postservice.service.comment;

import faang.school.postservice.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);

    CommentDto updateComment(CommentDto commentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long commentId);
}
