package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{id}")
    public LikeDto getLikeById(@PathVariable long id) {
        return likeService.getLikeById(id);
    }

    @GetMapping
    public List<LikeDto> getAllLikes() {
        return likeService.getAllLikes();
    }

    @PostMapping
    public LikeDto addLike(@RequestBody LikeDto likeDto) {
        return likeService.addLike(likeDto);
    }

    @DeleteMapping
    public void deleteLike(@RequestBody @Valid LikeDto likeDto) {
        likeService.deleteLike(likeDto);
    }

    @GetMapping("/users/{id}/post")
    public List<UserDto> getAllLikedByPostId(@PathVariable Long id) {
        return likeService.getAllLikedByPostId(id);
    }

    @GetMapping("/users/{id}/comment")
    public List<UserDto> getAllLikedByCommentId(@PathVariable Long id) {
        return likeService.getAllLikedByCommentId(id);
    }

}
