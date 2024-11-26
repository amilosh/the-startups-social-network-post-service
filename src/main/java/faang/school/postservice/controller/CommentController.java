package faang.school.postservice.controller;

import faang.school.postservice.docs.commet.CreateCommentDoc;
import faang.school.postservice.docs.commet.DeleteCommentDoc;
import faang.school.postservice.docs.commet.GetAllCommentDoc;
import faang.school.postservice.docs.commet.UpdateCommentDoc;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "This controller for comments")
public class CommentController {
    private final CommentService commentService;

    @CreateCommentDoc
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId,
            @Valid @RequestBody CreateCommentDto dto) {
        log.info("Request for new commit for the post: {} from user: {}", postId, dto.getAuthorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, dto));
    }

    @UpdateCommentDoc
    @PutMapping("/{postId}/comments")
    public  ResponseEntity<CommentResponseDto> update(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId,
            @Valid @RequestBody UpdateCommentDto dto) {
        log.info("Request for update of the comment: {} for the post: {}", dto.getId(), postId);
        return ResponseEntity.ok(commentService.updateComment(postId, dto));
    }

    @GetAllCommentDoc
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getAllComments(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId) {
        log.info("Request for all comments for the post: {}", postId);
        return ResponseEntity.ok(commentService.getAllComments(postId));
    }

    @DeleteCommentDoc
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable @Positive(message = "Post id should be a positive number") long postId,
            @PathVariable @Positive(message = "Comment id should be a positive number") long commentId) {
        log.info("Request for delete of comment: {} from post: {} received", commentId, postId);
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }

}
