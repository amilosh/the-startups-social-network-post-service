package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create post", description = "Returns created post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post author id must be the same as user id"
                    ))
            ),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "User or project not found"
                    )
            ))
    })
    public ResponseEntity<ResponsePostDto> create(@Valid @RequestBody CreatePostDto createPostDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(createPostDto));
    }

    @PutMapping("{postId}/publish")
    @Operation(summary = "Publish post", description = "Returns published post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post is not ready to publish"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post not found"
                    )
            ))
    })
    public ResponseEntity<ResponsePostDto> publish(
            @Valid
            @PathVariable
            @Positive(message = "Post id must be positive")
            @NotNull(message = "Post id cannot be null")
            Long postId
    ) {
        return ResponseEntity.ok(postService.publish(postId));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update post", description = "Returns updated post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post author id must be the same as user id"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "User or project not found"
                    )
            ))
    })
    public ResponseEntity<ResponsePostDto> update(
            @Valid
            @PathVariable
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive")
            Long postId,
            @Valid @RequestBody UpdatePostDto updatePostDto
    ) {
        return ResponseEntity.ok(postService.update(postId, updatePostDto));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Returns deleted post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post is not ready to delete"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post not found"
                    )
            ))
    })
    public ResponseEntity<Void> delete(
            @PathVariable
            @Valid
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long postId
    ) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post", description = "Returns post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Post not found"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                        value = "Post not found"
                )
            ))
    })
    public ResponseEntity<ResponsePostDto> getPostById(
            @PathVariable
            @Valid
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long postId
    ) {
        return ResponseEntity.ok(postService.getById(postId));
    }

    @GetMapping("/draft/author/{userId}")
    @Operation(summary = "Get drafts by user id", description = "Returns drafts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "User not found"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "User not found"
                    )
            ))
    })
    public ResponseEntity<List<ResponsePostDto>> getDraftByUserId(
            @PathVariable
            @Valid
            @NotNull(message = "User id cannot be null")
            @Positive(message = "User id must be positive") Long userId
    ) {
        return ResponseEntity.ok(postService.getDraftsByUserId(userId));
    }

    @GetMapping("/draft/project/{projectId}")
    @Operation(summary = "Get drafts by project id", description = "Returns deleted post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Project not found"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Project not found"
                    )
            ))
    })
    public ResponseEntity<List<ResponsePostDto>> getDraftByProjectId(
            @PathVariable
            @Valid
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long projectId
    ) {
        return ResponseEntity.ok(postService.getDraftsByProjectId(projectId));
    }

    @GetMapping("/published/author/{userId}")
    @Operation(summary = "Get published by user id", description = "Returns published post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "User not found"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "User not found"
                    )
            ))
    })
    public ResponseEntity<List<ResponsePostDto>> getPublishedByUserId(
            @Valid
            @PathVariable
            @NotNull(message = "User id cannot be null")
            @Positive(message = "User id must be positive")
            Long userId
    ) {
        return ResponseEntity.ok(postService.getPublishedByUserId(userId));
    }

    @GetMapping("/published/project/{projectId}")
    @Operation(summary = "Get published by project id", description = "Returns published post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Project not found"
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                            value = "Project not found"
                    )
            ))
    })
    public ResponseEntity<List<ResponsePostDto>> getPublishedByProjectId(
            @Valid
            @PathVariable
            @NotNull(message = "Project id cannot be null")
            @Positive(message = "Project id must be positive")
            Long projectId
    ) {
        return ResponseEntity.ok(postService.getPublishedByProjectId(projectId));
    }
}
