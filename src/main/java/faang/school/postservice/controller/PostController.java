package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@Valid @RequestBody PostRequestDto postRequestDtoDto) {
        return postService.createPost(postRequestDtoDto);
    }

    @PutMapping("/{postId}")
    public PostDto publishPost(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping()
    public PostDto updatePost(@Valid @RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PatchMapping("/{postId}/disable")
    public void disablePostById(@PathVariable Long postId) {
        postService.disablePostById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/drafts/users/{userId}")
    public List<PostDto> getAllNoPublishPostsByUserId(@PathVariable Long userId) {
        return postService.getAllNoPublishPostsByUserId(userId);
    }

    @GetMapping("/drafts/projects/{projectId}")
    public List<PostDto> getAllNoPublishPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllNoPublishPostsByProjectId(projectId);
    }

    @GetMapping("/users/{userId}")
    public List<PostDto> getAllPostsByUserId(@PathVariable Long userId) {
        return postService.getAllPostsByUserId(userId);
    }

    @GetMapping("/projects/{projectId}")
    public List<PostDto> getAllPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}