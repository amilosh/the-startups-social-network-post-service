package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.comment.CommentRequestDto;
import faang.school.postservice.model.dto.comment.CommentResponseDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto create(@Validated @RequestBody CommentRequestDto dto) {
        log.info("Incoming request: {}", dto);
        long userId = userContext.getUserId();
        return commentService.create(userId, dto);
    }

    @PutMapping
    public CommentResponseDto update(@Validated @RequestBody CommentRequestDto dto) {
        return commentService.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @Positive Long id) {
        commentService.delete(id);
    }

    @GetMapping("/postId/{postId}")
    public List<CommentResponseDto> findAll(@PathVariable("postId") @Positive Long postId) {
        return commentService.findAll(postId);
    }
}