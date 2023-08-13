package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likePost(LikeDto likeDto) {
        return likeService.createLikeOnPost(likeDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likeComment(LikeDto likeDto) {
        return likeService.createLikeOnComment(likeDto);
    }

    @DeleteMapping("/deleteLikePost")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeOnPost(LikeDto likeDto) {
        likeService.deleteLikeOnPost(likeDto);
    }

    @DeleteMapping("/deleteLikeComment")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeOnComment(LikeDto likeDto) {
        likeService.deleteLikeOnComment(likeDto);
    }

    @GetMapping("/getLikes")
    @ResponseStatus(HttpStatus.OK)
    public List<LikeDto> getAllPostLikes(LikeDto likeDto) {
        return likeService.getAllPostLikes(likeDto);
    }
}
