package faang.school.postservice.controller.ike;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{userId}")
    public void addLikeToPost(@PathVariable long userId,
                              @RequestBody @Valid LikeDto like) {
        likeService.addLikeToPost(userId, like);
    }

    @PostMapping("/comments/{userId}")
    public void addLikeToComment(@PathVariable long userId,
                                 @RequestBody @Valid LikeDto like) {
        likeService.addLikeToComment(userId, like);
    }

    @DeleteMapping("/posts/{userId}")
    public void removeLikeFromPost(@PathVariable long userId,
                                   @RequestBody @Valid LikeDto dto) {
        likeService.removeLikeFromPost(userId, dto);
    }

    @DeleteMapping("/comments/{userId}")
    public void removeLikeFromComment(@PathVariable long userId,
                                      @RequestBody @Valid LikeDto dto) {
        likeService.removeLikeFromComment(userId, dto);
    }
}
