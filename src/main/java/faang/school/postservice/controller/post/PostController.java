package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;
    private final ResourceService resourceService;
    private final ResourceValidator resourceValidator;

    @PostMapping("/drafts")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        postValidator.validateAuthorCount(postDto);
        postValidator.validateContentExists(postDto.getContent());

        return postService.createDraftPost(postDto);
    }

    @PutMapping("/drafts/{id}")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @PutMapping("/{id}")
    public PostDto updatePost(@RequestBody UpdatePostDto postDto, @PathVariable long id) {
        postValidator.validateContentExists(postDto.getContent());

        return postService.updatePost(postDto, id);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id) {
        postService.deletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getDraftsByUser(@PathVariable long userId) {
        return postService.getDraftsByUser(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getDraftsByProject(@PathVariable long projectId) {
        return postService.getDraftsByProject(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getPublishedPostsByUser(@PathVariable long userId) {
        return postService.getPublishedPostsByUser(userId);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDto> getPublishedPostsByProject(@PathVariable long projectId) {
        return postService.getPublishedPostsByProject(projectId);
    }

    @PostMapping("/{postId}/resources/add")
    public String addResource(@PathVariable Long postId, @RequestParam("file") MultipartFile file) {
        resourceValidator.validateResourceType(file);
        resourceService.addResource(postId, file);
        return "File uploader successfully";
    }
}