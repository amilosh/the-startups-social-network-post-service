package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
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
    public PostDto createPost(@Valid @RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping("/{postId}")
    public PostDto publishPost(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping()
    public PostDto updatePost(@Valid @RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/{postId}/delete")
    public void deletePostById(@PathVariable Long postId) {
        postService.deletePostById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/user-drafts/{userId}")
    public List<PostDto> getAllNoPublishPostByUserId(@PathVariable Long userId) {
        return postService.getAllNoPublishPostByUserId(userId);
    }

    @GetMapping("/project-drafts/{projectId}")
    public List<PostDto> getAllNoPublishPostByProjectId(@PathVariable Long projectId) {
        return postService.getAllNoPublishPostByProjectId(projectId);
    }

    @GetMapping("/user-posts/{userId}")
    public List<PostDto> getAllPostByUserId(@PathVariable Long userId) {
        return postService.getAllPostByUserId(userId);
    }

    @GetMapping("/project-posts/{projectId}")
    public List<PostDto> getAllPostByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostByProjectId(projectId);
    }
}