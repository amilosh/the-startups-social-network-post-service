package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/post")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public CommentDto addComment(@PathVariable @NotNull @Positive Long postId,
                                 @RequestBody @Valid CommentDto comment) {
        return commentService.addComment(postId, comment);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable @NotNull @Positive Long commentId,
                                    @RequestBody @NotBlank @Size(max = 4096) String content) {
        return commentService.updateComment(commentId, content);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentDto> getPostComments(@PathVariable @NotNull @Positive Long postId) {
        return commentService.getPostComments(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @NotNull @Positive Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
