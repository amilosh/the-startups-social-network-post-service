package faang.school.postservice.controller;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createPostDraft(@Valid @RequestBody PostDto postDto) {
        log.info("Received request to create a post by the user with ID: {}", postDto.getAuthorId());
        return postService.createPostDraft(postDto);
    }

    @PatchMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable Long postId) {
        log.info("Received request to publish the post with ID: {}", postId);
        return postService.publishPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostDto updatePost(@PathVariable Long postId,
                              @RequestBody PostDto postDto) {
        log.info("Received request to update the post with ID: {}", postId);
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public void softDelete(@PathVariable Long postId) {
        log.info("Received request to delete softly the post with ID: {}", postId);
        postService.softDelete(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        log.info("Received request to fetch the post with ID: {}", postId);
        return postService.getPostById(postId);
    }

    @GetMapping("/users/{userId}/drafts")
    public List<PostDto> getAllPostDraftsByUserId(@PathVariable Long userId) {
        log.info("Received request to fetch all post drafts for the user with ID: {}", userId);
        return postService.getAllPostDraftsByUserId(userId);
    }

    @GetMapping("/projects/{projectId}/drafts")
    public List<PostDto> getAllPostDraftsByProjectId(@PathVariable Long projectId) {
        log.info("Received request to fetch all post drafts for the project with ID: {}", projectId);
        return postService.getAllPostDraftsByProjectId(projectId);
    }

    @GetMapping("/users/{userId}")
    public List<PostDto> getAllPublishedPostsByUserId(@PathVariable Long userId) {
        log.info("Received request to fetch all published posts for the user with ID: {}", userId);
        return postService.getAllPublishedPostsByUserId(userId);
    }

    @GetMapping("/projects/{projectId}")
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable Long projectId) {
        log.info("Received request to fetch all published posts for the project with ID: {}", projectId);
        return postService.getAllPublishedPostsByProjectId(projectId);
    }
}
