package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.service.like.LikeService;
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
    public LikePostDto likePost(@RequestBody @Validated(LikePostDto.Before.class) LikePostDto likePostDto){
        log.info("Received a request to like a post: {} ", likePostDto.postId());
        return likeService.likePost(likePostDto);
    }

    @DeleteMapping("/posts/{likeId}")
    public void unlikePost(@PathVariable Long likeId){
        log.info("Received a request to unlike a post: {} ", likeId);
        likeService.unlikePost(likeId);
    }

    @PostMapping("/comments")
    public LikeCommentDto likeComment(@RequestBody @Validated(LikeCommentDto.Before.class) LikeCommentDto likeCommentDto){
        log.info("Received a request to like a comment: {} ", likeCommentDto.postId());
        return likeService.likeComment(likeCommentDto);
    }

    @DeleteMapping("/comments/{likeId}")
    public void unlikeComment(@PathVariable Long likeId){
        log.info("Received a request to unlike a comment: {} ", likeId);
        likeService.unlikeComment(likeId);
    }
}
