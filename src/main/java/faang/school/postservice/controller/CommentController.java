package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/comment/create")
    public CommentDto createComment(@RequestBody @Valid CommentDto comment) {
        return commentService.createComment(comment);
    }

    @PutMapping("/comment/update")
    public CommentDto updateComment(@RequestBody @Valid CommentDto comment) {
        return commentService.updateComment(comment);
    }

    @GetMapping("/post/{postId}/comments")
    public List<CommentDto> getComments(@PathVariable long postId) {
        return commentService.getComments(postId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }

}
