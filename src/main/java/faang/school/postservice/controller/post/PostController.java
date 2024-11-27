package faang.school.postservice.controller.post;


import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
        log.info("Received request to create post. AuthorId is {}, projectId is {}, resourceCount is {}",
                postDto.getAuthorId(), postDto.getProjectId(), postDto.getResources().size());
        PostResponseDto responseDto = postService.createPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}