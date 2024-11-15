package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.dto.comment.CommentDtoOutput;
import faang.school.postservice.dto.comment.CommentDtoOutputUponUpdate;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    public CommentDtoOutput create(CommentDtoInput input) {
        commentValidator.validateCommentDtoInput(input);
        return commentService.createComment(input);
    }

    public CommentDtoOutputUponUpdate update(CommentUpdateDto updatingInput) {
        commentValidator.validateCommentUpdateDto(updatingInput);
        return commentService.updateComment(updatingInput);
    }

    public List<CommentDtoOutput> getCommentsByPostId(Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    public void deleteComment(Long commentId) {
        commentService.deleteComment(commentId);
    }
}
