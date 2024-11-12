package faang.school.postservice.controller.like_controller;

import faang.school.postservice.dto.like.AcceptanceLikeDto;
import faang.school.postservice.dto.like.ReturnLikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ReturnLikeDto postLike(@RequestBody AcceptanceLikeDto likeDto, @PathVariable long postId) {
      return likeService.postLike(likeDto, postId);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@RequestBody AcceptanceLikeDto likeDto, @PathVariable long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    @PostMapping("/comment/{commentId}")
    public ReturnLikeDto commentLike(@RequestBody AcceptanceLikeDto likeDto, @PathVariable long commentId) {
        return likeService.commentLike(likeDto, commentId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@RequestBody AcceptanceLikeDto likeDto, @PathVariable long commentId) {
         likeService.deleteLikeFromComment(likeDto, commentId);
    }
}
