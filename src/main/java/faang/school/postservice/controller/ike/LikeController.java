package faang.school.postservice.controller.ike;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{userId}")
    public void addLikeToPost(@PathVariable long userId, @RequestParam long postId,
                              @RequestBody LikeDto like) {
        likeService.addLikeToPost(userId, postId, like);
    }

    @PostMapping("/comments/{userId}")
    public void addLikeToComment(@PathVariable long userId, @RequestParam long commentId,
                                 @RequestBody LikeDto like) {
        likeService.addLikeToComment(userId, commentId, like);
    }

    @PostMapping("/posts/{userId}")
    public void removeLikeFromPost(@PathVariable long userId, @RequestParam long likeId) {
        likeService.removeLikeFromPost(userId, likeId);
    }

    @PostMapping("/comments/{userId}")
    public void removeLikeFromComment(@PathVariable long userId, @RequestParam long likeId) {
        likeService.removeLikeFromComment(userId, likeId);
    }
}
