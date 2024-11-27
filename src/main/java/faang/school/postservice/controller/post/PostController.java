package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping()
    public PostResponseDto createPost(@RequestBody PostRequestDto postRequestDto) {
        return postService.create(postRequestDto);
    }

    @PutMapping("{id}/publish")
    public PostDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PutMapping()
    public PostDto updatePost(@RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @ModelAttribute
    public List<PostDto> getPost(@RequestBody PostFilterDto postFilterDto){
        return postService.getPosts(postFilterDto);
    }


}
