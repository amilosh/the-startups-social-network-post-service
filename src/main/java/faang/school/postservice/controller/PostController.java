package faang.school.postservice.controller;

//import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ReturnPostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping
//    public ReturnPostDto create(@RequestBody @Valid PostDto postDto) {
//        return postService.createPost(postDto);
//    }

    @PutMapping("/{id}")
    public ReturnPostDto publish(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PutMapping("/delete/{id}")
    public ReturnPostDto markDeleted(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @PutMapping
//    public ReturnPostDto update(@RequestBody PostDto postDto) {
//        return postService.updatePost(postDto);
//    }

    @GetMapping("/{id}")
    public ReturnPostDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/author_posts/{id}")
    public List<ReturnPostDto> getAllNonPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllNonPublishedByAuthorId(id);
    }

    @GetMapping("/project_posts/{id}")
    public List<ReturnPostDto> getAllNonPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllNonPublishedByProjectId(id);
    }

    @GetMapping("/published_by_user/{id}")
    public List<ReturnPostDto> getAllPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllPublishedByAuthorId(id);
    }

    @GetMapping("/published_by_project/{id}")
    public List<ReturnPostDto> getAllPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllPublishedByProjectId(id);
    }
}
