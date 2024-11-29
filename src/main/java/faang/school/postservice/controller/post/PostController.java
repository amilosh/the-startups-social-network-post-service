package faang.school.postservice.controller.post;


import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Tag(name = "Post Controller", description = "Controller for managing posts")
@ApiResponse(responseCode = "200", description = "Post successfully updated")
@ApiResponse(responseCode = "201", description = "Post successfully created")
@ApiResponse(responseCode = "204", description = "Post successfully deleted")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Post not found")
@ApiResponse(responseCode = "500", description = "Server Error")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(@ModelAttribute @Valid PostRequestDto postDto) {
        log.info("Received request to create post. AuthorId is {}, ProjectId is {}, Images: {}, Audio: {}",
                postDto.getAuthorId(),
                postDto.getProjectId(),
                postDto.getImages() != null ? postDto.getImages().size() : 0,
                postDto.getAudio() != null ? postDto.getAudio().size() : 0);

        PostResponseDto responseDto = postService.createPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @ModelAttribute @Valid PostUpdateDto postDto
    ) {
        log.info("Received request to update post with ID {}. Content: {}, Images: {}, Audio: {}",
                postId,
                postDto.getContent(),
                postDto.getImages() != null ? postDto.getImages().size() : 0,
                postDto.getAudio() != null ? postDto.getAudio().size() : 0);

        PostResponseDto responseDto = postService.updatePost(postId, postDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId) {
        PostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        log.info("Deleted post with ID {}", postId);
        return ResponseEntity.noContent().build();
    }
}