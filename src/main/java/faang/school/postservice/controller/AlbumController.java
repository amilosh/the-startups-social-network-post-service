package faang.school.postservice.controller;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@Valid @RequestBody AlbumDto albumDto) {
        log.info("Creating album {} by UserId {}", albumDto.getTitle(), albumDto.getAuthorId());

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(albumDto));
    }

    @PostMapping("/{albumId}/userId/{userId}/posts/{postId}")
    public ResponseEntity<AlbumDto> addPostToAlbum(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId,
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            long albumId,
            @PathVariable @Positive @NotNull(message = "PostId must be greater than 0.")
            long postId) {
        log.info("Adding post to album. UserId: {}, AlbumId: {}", userId, albumId);

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.addPostToAlbum(userId, albumId, postId));
    }

    @DeleteMapping("/{albumId}/userId/{userId}/posts/{postId}")
    public ResponseEntity<AlbumDto> removePostFromAlbum(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId,
            @PathVariable @Positive(message = "AlbumId must be greater than 0.")
            long albumId,
            @PathVariable @Positive @NotNull(message = "PostId must be greater than 0.")
            long postId) {
        log.info("Post removed from album. UserId: {}, AlbumId: {}", userId, albumId);

        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.removePost(userId, albumId, postId));
    }

    @PostMapping("/{albumId}/userId/{userId}/favorites")
    public ResponseEntity<AlbumDto> addAlbumToFavorites(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId,
            @PathVariable @Positive @NotNull(message = "PostId must be greater than 0.")
            long albumId) {
        return ResponseEntity.ok(albumService.addAlbumToFavorites(userId, albumId));
    }

    @DeleteMapping("/{albumId}/userId/{userId}/favorites")
    public ResponseEntity<Void> deleteAlbumFromFavorites(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId,
            @PathVariable @Positive @NotNull(message = "PostId must be greater than 0.")
            long albumId) {
        albumService.deleteAlbumFromFavorites(userId, albumId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumById(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long albumId) {
        return ResponseEntity.ok(albumService.findByAlbumId(albumId));
    }

    @PostMapping("user/{userId}/albums")
    public ResponseEntity<List<AlbumDto>> getUsersAlbumsWithFilters(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId,
            @Valid @RequestBody AlbumFilterDto filterDto) {
        return ResponseEntity.ok(albumService.getAlbumsForUserByFilter(userId, filterDto));
    }

    @PostMapping("/albums")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsWithFilters(
            @RequestBody AlbumFilterDto filterDto) {
        return ResponseEntity.ok(albumService.getAllAlbumsByFilter(filterDto));
    }

    @PostMapping("user/{userId}/favorites")
    public ResponseEntity<List<AlbumDto>> getFavoritUsersAlbumsWithFilters(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId,
            @Valid @RequestBody
            AlbumFilterDto filterDto) {
        return ResponseEntity.ok(albumService.getFavoritAlbumsForUserByFilter(userId, filterDto));
    }

    @PutMapping
    public ResponseEntity<AlbumDto> updateAlbum(
            @Valid @RequestBody AlbumUpdateDto albumDto) {
        return ResponseEntity.ok(albumService.updateAlbum(albumDto));
    }

    @DeleteMapping("/{albumId}/userId/{userId}")
    public ResponseEntity<Void> deleteAlbum(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long albumId,
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.")
            long userId) {
        albumService.deleteAlbum(userId, albumId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
