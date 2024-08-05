package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.*;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EnableFeignClients
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createDraft(@ModelAttribute @Valid DraftPostDto draft) {
        return postService.createPostDraft(draft);
    }

    @PutMapping("/{id}")
    public PostDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PatchMapping
    public PostDto updatePost(@ModelAttribute UpdatablePostDto updatablePost) {
        return postService.updatePost(updatablePost);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return postService.findPost(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/list")
    public List<PostDto> getPosts(
            @RequestParam(value = "author_id", required = false) Long authorId,
            @RequestParam(value = "project_id", required = false) Long projectId,
            @RequestParam(value = "post_status") PostStatus postStatus
    ) {

        GetPostsDto getPostRequest = new GetPostsDto(
                authorId,
                projectId,
                postStatus
        );

        return postService.getPosts(getPostRequest);
    }
}
