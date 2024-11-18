package faang.school.postservice.controller.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.validator.album.AlbumValidator;
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
    private final AlbumValidator validator;
    private final UserContext userContext;

    @PostMapping
    public AlbumResponseDto createAlbum(@RequestBody AlbumRequestDto albumDto) {
        return albumService.createAlbum(albumDto);
    }

    @GetMapping("/{albumId}/post/{postId}")
    public AlbumResponseDto addPost(@PathVariable long albumId, @PathVariable long postId) {
        validator.validateAuthorHasThisAlbum(albumId, userContext.getUserId());
        return albumService.addPost(albumId, postId);
    }

    @DeleteMapping("/{albumId}/post/{postId}")
    public void deletePost(@PathVariable long albumId, @PathVariable long postId) {
        validator.validateAuthorHasThisAlbum(albumId, userContext.getUserId());
        albumService.deletePost(albumId, postId);
    }

    @PostMapping("/{albumId}/author/favorite")
    public void addAlbumToFavoriteAlbums(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        validator.validateAuthorHasThisAlbum(albumId, userId);
        albumService.addAlbumToFavoriteAlbums(albumId, userId);
    }

    @DeleteMapping("/{albumId}/author/favorite")
    public void deleteAlbumFromFavoriteAlbums(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        validator.validateAuthorHasThisAlbum(albumId, userId);
        albumService.deleteAlbumFromFavoriteAlbums(albumId, userId);
    }

    @GetMapping("/{albumId}")
    public AlbumResponseDto getAlbum(@PathVariable long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping("/author")
    public List<AlbumResponseDto> getAllMyAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter) {
        return albumService.getAllMyAlbumsByFilter(albumFilter, userContext.getUserId());
    }

    @GetMapping
    public List<AlbumResponseDto> getAllAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter) {
        return albumService.getAllAlbumsByFilter(albumFilter);
    }

    @GetMapping("author/favorite")
    public List<AlbumResponseDto> getAllFavoriteAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter) {
        return albumService.getAllFavoriteAlbumsByFilter(albumFilter, userContext.getUserId());
    }

    @PutMapping
    public AlbumResponseDto updateAlbum(@RequestBody AlbumRequestUpdateDto albumRequestUpdateDto) {
        return albumService.updateAlbum(albumRequestUpdateDto, userContext.getUserId());
    }

    @DeleteMapping("/{albumId}")
    public void deleteAlbum(@PathVariable long albumId) {
        long userId = userContext.getUserId();
        validator.validateAuthorHasThisAlbum(albumId, userId);
        albumService.deleteAlbum(albumId, userId);
    }

}
