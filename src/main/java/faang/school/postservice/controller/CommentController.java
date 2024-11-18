package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CommentControllerValidator validator;

    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        validator.validateCommentDto(commentDto);
        return commentService.createComment(commentDto);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        validator.validateCommentDto(commentDto);
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        validator.validatePostId(postId);
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        validator.validateCommentId(commentId);
        commentService.deleteComment(commentId);
    }
}
