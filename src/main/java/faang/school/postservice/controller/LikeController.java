package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikePostDto;

import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/like")
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;

    @PostMapping("/{postId}")
    public LikePostDto likePost(
            @PathVariable @Positive long postId,
            @RequestBody @Validated LikePostDto likeDto) {
        long userId = userContext.getUserId();
        return likeService.likePost(postId, likeDto);
    }
}
