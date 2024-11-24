package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.utilities.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.COMMENT)
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(CommentDto commentDto) {

        return commentService.createComment(commentDto);
    }

    @PutMapping(UrlUtils.ID)
    public CommentDto updateComment(@PathVariable("id") Long id, CommentDto commentDto) {

        return commentService.updateComment(id, commentDto);
    }

    @GetMapping(UrlUtils.ID)
    public List<CommentDto> getAllComments(@PathVariable("id") Long id) {

        return commentService.getAllComments(id);
    }

    @DeleteMapping(UrlUtils.ID)
    public void deleteComment(@PathVariable("id") Long id, @RequestParam() Long commentId) {
        commentService.deleteComment(id, commentId);
    }
}
