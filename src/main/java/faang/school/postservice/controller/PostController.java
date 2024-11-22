package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public PostDto create(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PatchMapping("/publish/{id}")
    public PostDto publish(@PathVariable @Positive long id) {
        return postService.publishPost(id);
    }

    @PostMapping("/update")
    public PostDto update(@RequestBody UpdatePostDto updatePostDto) {
        return postService.updatePost(updatePostDto);
    }

    @GetMapping("/{id}")
    public PostDto getById(@PathVariable @Positive long id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/delete/{id}")
    public PostDto delete(@PathVariable @Positive long id) {
        return postService.deletePost(id);
    }

    @GetMapping("/get/draft/byUser/{userId}")
    public List<PostDto> getAllDraftNotDeletedPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getAllDraftNotDeletedPostsByUserId(userId);
    }

    @GetMapping("/get/draft/byProject/{projectId}")
    public List<PostDto> getAllDraftNotDeletedPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getAllDraftNotDeletedPostsByProjectId(projectId);
    }

    @GetMapping("/get/published/byUser/{userId}")
    public List<PostDto> getAllPublishedNotDeletedPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getAllPublishedNotDeletedPostsByUserId(userId);
    }

    @GetMapping("/get/published/byProject/{projectId}")
    public List<PostDto> getAllPublishedNotDeletedPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getAllPublishedNotDeletedPostsByProjectId(projectId);
    }
}
