package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.RequestCommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @PostMapping()
    public ResponseCommentDto create(@RequestBody RequestCommentDto input) {
        commentValidator.validateCommentIdIsNullForCreatingNewComment(input);
        commentValidator.validateCommentDtoInput(input);
        return commentService.createComment(input);
    }

    @PutMapping()
    public ResponseCommentDto update(@RequestBody RequestCommentUpdateDto updatingInput) {
        commentValidator.validateCommentUpdateDto(updatingInput);
        return commentService.updateComment(updatingInput);
    }

    @GetMapping()
    public List<ResponseCommentDto> getCommentsByPostId(@RequestParam Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}