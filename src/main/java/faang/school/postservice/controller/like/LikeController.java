package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts")
    public LikePostDto likePost(@RequestBody @Validated({LikePostDto.Before.class, LikePostDto.After.class}) LikePostDto likePostDto){
        log.info("Received a request to like a post: {} ", likePostDto.postId());
        return likeService.likePost(likePostDto);
    }

    @Validated
    @DeleteMapping("/posts/{postId}/users/{userId}")
    public void unlikePost(@PathVariable @NotNull Long postId,
                           @PathVariable @NotNull Long userId){
        log.info("Received a request to unlike a post: {} ", postId);
        likeService.unlikePost(postId, userId);
    }

    @PostMapping("/comments")
    public LikeCommentDto likeComment(@RequestBody @Validated({LikeCommentDto.Before.class, LikeCommentDto.After.class}) LikeCommentDto likeCommentDto){
        log.info("Received a request to like a comment: {} ", likeCommentDto.postId());
        return likeService.likeComment(likeCommentDto);
    }

    @Validated
    @DeleteMapping("/comments/{commentId}/users/{userId}")
    public void unlikeComment(@PathVariable @NotNull Long commentId,
                              @PathVariable @NotNull Long userId){
        log.info("Received a request to unlike a comment: {} ", commentId);
        likeService.unlikeComment(commentId, userId);
    }
}
