package faang.school.postservice.service.comment;

import faang.school.postservice.dto.CommentDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);

    CommentDto updateComment(@RequestBody CommentDto commentDto);

    List<CommentDto> getCommentsByPostId(@PathVariable Long postId);

    void deleteComment(@PathVariable Long commentId);
}
