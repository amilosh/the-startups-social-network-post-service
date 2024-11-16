package faang.school.postservice.controller.album;

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
@RequestMapping("/api/v1/album")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumValidator validator;

    @PostMapping
    public AlbumResponseDto createAlbum(@RequestBody AlbumRequestDto albumDto) {
        validator.validateAlbum(albumDto);
        return albumService.createAlbum(albumDto);
    }

    @GetMapping("/post/{postId}")
    public AlbumResponseDto addPost(@RequestBody AlbumRequestDto albumDto, @PathVariable long postId) {
        return albumService.addPost(albumDto, postId);
    }

    @DeleteMapping("/post/{postId}")
    public void deletePost(@RequestBody AlbumRequestDto albumDto, @PathVariable long postId) {
        albumService.deletePost(albumDto, postId);
    }

    @PostMapping("/author/{authorId}/favorite/{albumId}")
    public void addAlbumToFavoriteAlbums(@PathVariable long albumId, @PathVariable long authorId) {
        albumService.addAlbumToFavoriteAlbums(albumId, authorId);
    }

    @DeleteMapping("/author/{authorId}/favorite/{albumId}")
    public void deleteAlbumFromFavoriteAlbums(@PathVariable long albumId, @PathVariable long authorId) {
        albumService.deleteAlbumFromFavoriteAlbums(albumId, authorId);
    }

    @GetMapping("/{albumId}")
    public AlbumResponseDto getAlbum(@PathVariable long albumId) {
        return albumService.getAlbum(albumId);
    }

    @GetMapping("/author/{authorId}")
    public List<AlbumResponseDto> getAllMyAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter, @PathVariable long authorId) {
        return albumService.getAllMyAlbumsByFilter(albumFilter, authorId);
    }

    @GetMapping
    public List<AlbumResponseDto> getAllAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter) {
        return albumService.getAllAlbumsByFilter(albumFilter);
    }

    @GetMapping("/author/{authorId}/favorite")
    public List<AlbumResponseDto> getAllFavoriteAlbumsByFilter(@ModelAttribute AlbumFilterDto albumFilter, @PathVariable long authorId) {
        return albumService.getAllFavoriteAlbumsByFilter(albumFilter, authorId);
    }

    @PutMapping
    public AlbumResponseDto updateAlbum(@RequestBody AlbumRequestUpdateDto albumRequestUpdateDto) {
        validator.validateAlbum(albumRequestUpdateDto);
        return albumService.updateAlbum(albumRequestUpdateDto);
    }

    @DeleteMapping("/author/{authorId}/{albumId}")
    public void deleteAlbum(@PathVariable long albumId, @PathVariable long authorId) {
        albumService.deleteAlbum(albumId, authorId);
    }

}
