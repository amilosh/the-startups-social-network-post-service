package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/api/post-service/v1/posts/{postId}/like")
    public LikeDto likePost(@PathVariable long postId, @Valid @RequestBody LikeDto like) {
        return likeService.likePost(postId, like);
    }

    @DeleteMapping("/api/post-service/v1/posts/{postId}/like")
    public void unlikePost(@PathVariable long postId, @Valid @RequestBody LikeDto like) {
        likeService.unlikePost(postId, like);
    }

    @PostMapping("/api/post-service/v1/comment/{commentId}/like")
    public LikeDto likeComment(@PathVariable long commentId, @Valid @RequestBody LikeDto like) {
        return likeService.likeComment(commentId, like);
    }

    @DeleteMapping("/api/post-service/v1/comment/{commentId}/like")
    public void unlikeComment(@PathVariable long commentId, @Valid @RequestBody LikeDto like) {
        likeService.unlikeComment(commentId, like);
    }
}
