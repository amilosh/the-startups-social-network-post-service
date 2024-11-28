package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.post.post_controller.ValidUploadFiles;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
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

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<byte[]> uploadFile(@RequestPart("files") @ValidUploadFiles MultipartFile[] files) throws IOException {
        MultipartFile file = files[0];

        // Проверяем MIME-тип файла
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Неверный тип файла
        }

        // Преобразуем файл в byte[]
        byte[] imageBytes = file.getBytes();

        // Возвращаем byte[] с правильным типом контента
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }
}