package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.dto.comment.CommentDtoOutput;
import faang.school.postservice.dto.comment.CommentDtoOutputUponUpdate;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @PostMapping("/comments")
    public CommentDtoOutput create(@RequestBody CommentDtoInput input) {
        commentValidator.validateCommentDtoInput(input);
        return commentService.createComment(input);
    }

    @PostMapping("comments")
    public CommentDtoOutputUponUpdate update(@RequestBody CommentUpdateDto updatingInput) {
        commentValidator.validateCommentUpdateDto(updatingInput);
        return commentService.updateComment(updatingInput);
    }

    @GetMapping("/comments")
    public List<CommentDtoOutput> getCommentsByPostId(@RequestParam("postId") Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }
}
