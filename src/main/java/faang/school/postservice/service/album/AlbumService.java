package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostMapper postMapper;
    private final List<AlbumFilter> albumFilters;
    private final UserServiceClient userServiceClient;
    private final PostService postService;


    public AlbumDto createAlbum(AlbumDto albumDto) {
        Album album = albumMapper.toEntity(albumDto);
        return albumMapper.toDto(albumRepository.save(album));
    }

    public AlbumDto add(long postId, long albumId, long authorId) {
        validateUserExist(authorId);
        Album preciseAlbum = albumRepository.findByAuthorId(authorId)
                .filter(album -> album.getId() == albumId).findFirst()
                .orElseThrow(EntityNotFoundException::new);
        PostDto postDto = postService.getPost(postId);
        Post post = postMapper.toEntity(postDto);
        preciseAlbum.addPost(post);
        return albumMapper.toDto(albumRepository.save(preciseAlbum));
    }

    public void addToFavorites(long albumId, long authorId) {
        validateUserExist(authorId);
        albumRepository.addAlbumToFavorites(albumId, authorId);
    }

    public void removeFromFavorites(long albumId, long authorId) {
        validateUserExist(authorId);
        albumRepository.deleteAlbumFromFavorites(albumId, authorId);
    }

    public AlbumDto getAlbum(long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow(EntityNotFoundException::new);
        return albumMapper.toDto(album);
    }

    public List<AlbumDto> getAlbums(long authorId) {
        validateUserExist(authorId);
        return albumRepository.findByAuthorId(authorId).map(albumMapper::toDto).toList();
    }


    public List<AlbumDto> getAlbumsWithFilter(long authorId, AlbumFilterDto albumFilterDto) {
        validateUserExist(authorId);
        if (albumFilterDto == null) {
            return albumRepository.findByAuthorId(authorId).map(albumMapper::toDto).toList();
        }
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        Stream<Album> filteredAlbums = filteredStream(albums, albumFilterDto);

        return filteredAlbums.map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getAllAlbumsWithFilter(AlbumFilterDto albumFilterDto) {
        if (albumFilterDto == null) {
            return albumRepository.findAll().stream().map(albumMapper::toDto).toList();
        }
        List<Album> albums = albumRepository.findAll();
        Stream<Album> filteredAlbums = filteredStream(albums.stream(), albumFilterDto);

        return filteredAlbums.map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getAllAlbums() {
        return albumRepository.findAll().stream().map(albumMapper::toDto).toList();
    }

    public List<AlbumDto> getFavoriteFilteredAlbums(long authorId, AlbumFilterDto albumFilterDto) {
        validateUserExist(authorId);
        if (albumFilterDto == null) {
            return albumRepository.findFavoriteAlbumsByUserId(authorId).map(albumMapper::toDto).toList();
        }
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        Stream<Album> filteredAlbums = filteredStream(albums, albumFilterDto);

        return filteredAlbums.map(albumMapper::toDto).toList();
    }

    public AlbumDto update(AlbumDto albumDto) {
        return albumMapper.toDto(albumRepository.save(albumMapper.toEntity(albumDto)));
    }

    public void remove(long albumId) {
        albumRepository.deleteById(albumId);
    }

    private void validateUserExist(Long id) {
        userServiceClient.getUser(id);
    }

    private Stream<Album> filteredStream(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albumFilters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .flatMap(albumFilter -> albumFilter.apply(albums, albumFilterDto));
    }
}
