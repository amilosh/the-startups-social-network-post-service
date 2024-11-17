package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.album.albumFilter.AlbumFilter;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper mapper;
    private final AlbumValidator validator;
    private final List<AlbumFilter> filters;

    public AlbumResponseDto createAlbum(AlbumRequestDto albumRequestDto) {
        long authorId = albumRequestDto.getAuthorId();
        String title = albumRequestDto.getTitle();
        validator.validateAuthor(authorId);
        validator.validateAlbumWithSameTitleExists(authorId, title);
        Album album = mapper.toAlbum(albumRequestDto);
        album.setPosts(new ArrayList<>());
        validator.validateAlbumExists(album.getId());
        albumRepository.save(album);
        log.info("Album saved: {}", album.getId());
        log.info("Album created: {}", album.getId());
        return mapper.toAlbumResponseDto(album);
    }

    public AlbumResponseDto addPost(AlbumRequestDto albumRequestDto, long postId) {
        validator.validateAlbumForPost(albumRequestDto);
        Post post = validator.validatePost(postId);
        long albumId = albumRequestDto.getId();
        Album album = validator.validateAlbumExists(albumId);
        validator.validateAuthorHasThisAlbum(album, albumRequestDto.getAuthorId());
        album.addPost(post);
        log.info("The post {} has been added to the album {}", postId, albumId);
        albumRepository.save(album);
        return mapper.toAlbumResponseDto(album);
    }

    public void deletePost(AlbumRequestDto albumRequestDto, long postId) {
        validator.validateAlbumForPost(albumRequestDto);
        Post post = validator.validatePost(postId);
        long albumId = albumRequestDto.getId();
        Album album = validator.validateAlbumExists(albumId);
        validator.validateAuthorHasThisAlbum(album, albumRequestDto.getAuthorId());
        album.removePost(post.getId());
        log.info("The post {} has been deleted from the album {}", postId, albumId);
        albumRepository.save(album);
    }

    public void addAlbumToFavoriteAlbums(long albumId, long authorId) {
        validator.validateAlbumExists(albumId);
        validator.validateAuthor(authorId);
        boolean result = validator.validateFavoritesHasThisAlbum(albumId, authorId);
        if (result) {
            throw new DataValidationException("Favorite album with id " + albumId + " already exists");
        }
        log.info("The album {} has been added to favorites", albumId);
        albumRepository.addAlbumToFavorites(albumId, authorId);
    }

    public void deleteAlbumFromFavoriteAlbums(long albumId, long authorId) {
        validator.validateAlbumExists(albumId);
        validator.validateAuthor(authorId);
        boolean result = validator.validateFavoritesHasThisAlbum(albumId, authorId);
        System.out.println(result);
        if (!result) {
            throw new DataValidationException("Favorite album with id " + albumId + " does not exist");
        }
        log.info("The album {} has been deleted from favorites", albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, authorId);
    }

    public AlbumResponseDto getAlbum(long albumId) {
       Album album = validator.validateAlbumExists(albumId);
       log.info("The album {} has been found", albumId);
       return mapper.toAlbumResponseDto(album);
    }

    public List<AlbumResponseDto> getAllMyAlbumsByFilter(AlbumFilterDto albumFilter, long authorId) {
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        filters.stream()
                .filter(filter -> filter.isApplicable(albumFilter))
                .forEach(filter -> filter.apply(albums, albumFilter));
        log.info("Retrieved an albums from the user's {} album list using filters", authorId);
        return mapper.toAlbumResponseDtoList(albums.toList());
    }

    public List<AlbumResponseDto> getAllAlbumsByFilter(AlbumFilterDto albumFilter) {
        Stream<Album> albums = albumRepository.findAllAlbums();
        filters.stream()
                .filter(filter -> filter.isApplicable(albumFilter))
                .forEach(filter -> filter.apply(albums, albumFilter));
        log.info("Retrieved an albums using filters");
        return mapper.toAlbumResponseDtoList(albums.toList());
    }

    public List<AlbumResponseDto> getAllFavoriteAlbumsByFilter(AlbumFilterDto albumFilter, long authorId) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        filters.stream()
                .filter(filter -> filter.isApplicable(albumFilter))
                .forEach(filter -> filter.apply(albums, albumFilter));
        log.info("Retrieved albums from the user's {} favorite albums list using filters", authorId);
        return mapper.toAlbumResponseDtoList(albums.toList());
    }

    public AlbumResponseDto updateAlbum(AlbumRequestUpdateDto albumRequestUpdateDto) {
        long authorId = albumRequestUpdateDto.getAuthorId();
        String title = albumRequestUpdateDto.getTitle();
        validator.validateAuthor(authorId);
        validator.validateAlbumWithSameTitleExists(authorId, title);
        Album oldAlbum = validator.validateAlbumExists(albumRequestUpdateDto.getId());
        Album newAlbum = mapper.toAlbum(albumRequestUpdateDto);
        List<Post> posts = getPosts(albumRequestUpdateDto.getPostsIds());
        newAlbum.setPosts(posts);
        newAlbum.setAuthorId(oldAlbum.getAuthorId());
        albumRepository.save(newAlbum);
        log.info("Album updated: {}", newAlbum.getId());
        return mapper.toAlbumResponseDto(newAlbum);
    }

    public void deleteAlbum(long albumId, long authorId) {
        Album album = validator.validateAlbumExists(albumId);
        validator.validateAuthor(authorId);
        validator.validateAuthorHasThisAlbum(album, authorId);
        albumRepository.deleteAlbum(albumId, authorId);
        log.info("The album {} has been deleted", albumId);
    }

    private List<Post> getPosts(List<Long> postsIds) {
      return postsIds.stream().map(validator::validatePost).toList();
    }

}
