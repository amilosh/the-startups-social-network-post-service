package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.post.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API for managing posts' albums")
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private AlbumService albumService;

    @Operation(description = "Create an empty album with given title and description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album was created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid title or description provided"),
            @ApiResponse(responseCode = "404", description = "User with the provided ID not found in the database")
    })
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long authorId,
            @RequestBody @Valid AlbumCreateDto createDto
    ) {
        AlbumDto responseDto = albumService.createAlbum(authorId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/{albumId}/{postId}")
    public ResponseEntity<AlbumDto> addPostToAlbum(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") long authorId,
            @PathVariable @Min(value = 1, message = "Album ID must be greater than 0!") long albumId,
            @PathVariable @Min(value = 1, message = "Post ID must be greater than 0!") long postId
    ) {

    }
}
