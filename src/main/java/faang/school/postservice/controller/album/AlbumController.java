package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumValidator validator;

    public AlbumResponseDto createAlbum(AlbumRequestDto albumDto) {
        validator.validateAlbum(albumDto);
        return albumService.createAlbum(albumDto);
    }

    public AlbumResponseDto addPost(AlbumRequestDto albumDto, long postId) {
       return albumService.addPost(albumDto, postId);
    }

    public void addAlbumToFavoriteAlbums(long albumId, long authorId) {
        albumService.addAlbumToFavoriteAlbums(albumId, authorId);
    }

    public void deleteAlbumFromFavoriteAlbums(long albumId, long authorId) {
        albumService.deleteAlbumFromFavoriteAlbums(albumId, authorId);
    }

    public AlbumResponseDto getAlbum(long albumId) {
        return albumService.getAlbum(albumId);
    }

    public List<AlbumResponseDto> getAllMyAlbumsByFilter(AlbumFilterDto albumFilter, long authorId) {
        return albumService.getAllMyAlbumsByFilter(albumFilter, authorId);
    }

    public List<AlbumResponseDto> getAllAlbumsByFilter(AlbumFilterDto albumFilter) {
        return albumService.getAllAlbumsByFilter(albumFilter);
    }

    public List<AlbumResponseDto> getAllFavoriteAlbumsByFilter(AlbumFilterDto albumFilter, long authorId) {
        return albumService.getAllFavoriteAlbumsByFilter(albumFilter, authorId);
    }

    public AlbumResponseDto updateAlbum(AlbumRequestUpdateDto albumRequestUpdateDto) {
        validator.validateAlbum(albumRequestUpdateDto);
        return albumService.updateAlbum(albumRequestUpdateDto);
    }

    public void deleteAlbum(long albumId, long authorId) {
        albumService.deleteAlbum(albumId, authorId);
    }

    public void deleteAlbumFromFavorite(long albumId, long authorId) {
        albumService.deleteAlbumFromFavorite(albumId, authorId);
    }

}
