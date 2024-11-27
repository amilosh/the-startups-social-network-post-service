package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostFilterDto;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import io.lettuce.core.dynamic.annotation.Value;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public PostResponseDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PutMapping()
    public PostResponseDto updatePost(@RequestBody PostUpdateDto postDto) {
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping
    public List<PostResponseDto> getPost(@Valid @ModelAttribute PostFilterDto postFilterDto){
        return postService.getPosts(postFilterDto);
    }


}
