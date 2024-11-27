package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final UserContext userContext;

    @PostMapping
    public AlbumResponseDto createAlbum(@Valid @RequestBody AlbumRequestDto albumDto) {
        return albumService.createAlbum(albumDto, userContext.getUserId());
    }

    @PostMapping("/{albumId}/post/{postId}")
    public AlbumResponseDto addPost(@PathVariable long albumId, @PathVariable long postId) {
        return albumService.addPost(albumId, postId, userContext.getUserId());
    }

    @DeleteMapping("/{albumId}/post/{postId}")
    public void deletePost(@PathVariable long albumId, @PathVariable long postId) {
        albumService.deletePost(albumId, postId, userContext.getUserId());
    }

    @PostMapping("/{albumId}/favorite")
    public void addAlbumToFavoriteAlbums(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.addAlbumToFavoriteAlbums(albumId, userId);
    }

    @DeleteMapping("/{albumId}/favorite")
    public void deleteAlbumFromFavoriteAlbums(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.deleteAlbumFromFavoriteAlbums(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumResponseDto getAlbum(@PathVariable long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping
    public List<AlbumResponseDto> getAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter) {
        return albumService.getAlbumsByFilter(albumFilter);
    }

    @GetMapping("favorite")
    public List<AlbumResponseDto> getAllFavoriteAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter) {
        return albumService.getAllFavoriteAlbumsByFilter(albumFilter, userContext.getUserId());
    }

    @PutMapping
    public AlbumResponseDto updateAlbum(@Valid @RequestBody AlbumRequestUpdateDto albumRequestUpdateDto) {
        return albumService.updateAlbum(albumRequestUpdateDto, userContext.getUserId());
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        albumService.deleteAlbum(albumId, userId);
    }

}
