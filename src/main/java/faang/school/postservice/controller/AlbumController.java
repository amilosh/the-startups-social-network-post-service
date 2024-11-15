package faang.school.postservice.controller;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.AlbumUpdateDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
@Slf4j
public class AlbumController {
    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@Valid @RequestBody AlbumDto albumDto) {
        log.info("Creating album {} by UserId {}", albumDto.getTitle(), albumDto.getAuthorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.createAlbum(albumDto));
    }

    @PostMapping("/{currentUserId}/album/{albumId}")
    public ResponseEntity<AlbumDto> addPostToAlboom(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long currentUserId,
            @PathVariable @Positive(message = "AlbumId must be greater than 0.") long albumId,
            @Valid @RequestBody PostDto postDto) {
        log.info("Adding post to album. UserId: {}, AlbumId: {}", currentUserId, albumId);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.addPostToAlboom(currentUserId, albumId, postDto));
    }

    @DeleteMapping("/{currentUserId}/album/{albumId}")
    public ResponseEntity<AlbumDto> removePostFromAlbum(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long currentUserId,
            @PathVariable @Positive(message = "AlbumId must be greater than 0.") long albumId,
            @Valid @RequestBody PostDto postDto) {
        log.info("Post removed from album. UserId: {}, AlbumId: {}", currentUserId, albumId);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.removePost(currentUserId, albumId, postDto));
    }

    @PostMapping("/favorites")
    public ResponseEntity<AlbumDto> addAlbumToFavorites(
            @RequestParam @Positive(message = "CurrentUserId must be greater than 0.") long userId,
            @Valid @RequestBody AlbumDto albumDto) {
        return ResponseEntity.ok(albumService.addAlbumToFavorites(userId, albumDto));
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<Void> deleteAlbumFromFavorites(
            @RequestParam @Positive(message = "CurrentUserId must be greater than 0.") long userId,
            @Valid @RequestBody AlbumDto albumDto) {
        albumService.deleteAlbumFromFavorites(userId, albumDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumById(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long albumId) {
        return ResponseEntity.ok(albumService.findByAlbumId(albumId));
    }

    @GetMapping("user/{currentUserId}/albums")
    public ResponseEntity<List<AlbumDto>> getUsersAlbumsWithFilters(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long currentUserId,
            @Valid @RequestBody AlbumFilterDto filterDto) {
        return ResponseEntity.ok(albumService.getAlbumsForUserByFilter(currentUserId, filterDto));
    }

    @GetMapping("/albums")
    public ResponseEntity<List<AlbumDto>> getAllAlbumsWithFilters(
            @RequestBody AlbumFilterDto filterDto) {
        return ResponseEntity.ok(albumService.getAllAlbumsByFilter(filterDto));
    }

    @GetMapping("user/{currentUserId}/favorites")
    public ResponseEntity<List<AlbumDto>> getFavoritUsersAlbumsWithFilters(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long currentUserId,
            @Valid @RequestBody AlbumFilterDto filterDto) {
        return ResponseEntity.ok(albumService.getFavoritAlbumsForUserByFilter(currentUserId, filterDto));
    }

    @PutMapping
    public ResponseEntity<AlbumDto> updateAlbum(@Valid @RequestBody AlbumUpdateDto albumDto) {
        return ResponseEntity.ok(albumService.updateAlbum(albumDto));
    }

    @DeleteMapping("user/{currentUserId}")
    public ResponseEntity<Void> deleteAlbum(
            @PathVariable @Positive(message = "CurrentUserId must be greater than 0.") long currentUserId,
            @Valid @RequestBody AlbumDto albumDto) {
        albumService.deleteAlbum(currentUserId, albumDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
