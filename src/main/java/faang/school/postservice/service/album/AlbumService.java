package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.FeignClientException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.exception.MessageError;
import faang.school.postservice.exception.UnauthorizedException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private static final String USER = "User";
    private static final String ALBUM = "Album";

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final PostService postService;
    private final List<AlbumFilter> filters;

    @Transactional
    public AlbumDto createAlbum(AlbumCreateUpdateDto createDto) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is attempting to create a new album with title '{}'", userId, createDto.getTitle());

        validateUserExistence(userId);
        validateAlbumTitle(createDto.getTitle(), userId);
        Album albumToSave = albumMapper.toEntity(createDto);
        albumToSave.setAuthorId(userId);
        Album savedAlbum = albumRepository.save(albumToSave);

        log.info("User with ID {} successfully created album with ID {} titled '{}'", userId, savedAlbum.getId(), createDto.getTitle());
        return albumMapper.toDtoList(savedAlbum);
    }

    @Transactional
    public AlbumDto addPostToAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is adding post with ID {} to album with ID {}", userId, postId, albumId);

        Album album = getAlbum(albumId);
        validateAlbumAuthor(album, userId, "add post to album with ID %d".formatted(albumId));
        Post post = postService.getPost(postId);
        album.addPost(post);
        Album savedAlbum = albumRepository.save(album);

        log.info("User with ID {} successfully added post with ID {} to album with ID {}", userId, postId, albumId);
        return albumMapper.toDtoList(savedAlbum);
    }

    @Transactional
    public void deletePostFromAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is attempting to remove post with ID {} from album with ID {}", userId, postId, albumId);

        Album album = getAlbum(albumId);
        validateAlbumAuthor(album, userId, "delete post from album with ID %d".formatted(albumId));

        if (album.getPosts().stream().anyMatch(post -> post.getId() == postId)) {
            album.removePost(postId);
            albumRepository.save(album);
            log.info("User with ID {} successfully removed post with ID {} from album with ID {}", userId, postId, albumId);
        } else {
            log.warn("User with ID {} attempted to remove non-existent post with ID {} from album with ID {}", userId, postId, albumId);
        }
    }

    @Transactional
    public void addAlbumToFavorites(long albumId) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is attempting to add album with ID: {} to favorites", userId, albumId);

        getAlbum(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);

        log.info("User with ID {} added album with ID {} to favorites", userId, albumId);
    }

    @Transactional
    public void deleteAlbumFromFavorites(long albumId) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is attempting to remove album with ID: {} to favorites", userId, albumId);

        getAlbum(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);

        log.info("User with ID {} removed album with ID {} from favorites", userId, albumId);
    }

    @Transactional
    public AlbumDto getAlbumById(long albumId) {
        log.info("User with ID {} is fetching album with ID {}", userContext.getUserId(), albumId);
        Album album = getAlbum(albumId);
        log.info("Album with ID {} successfully fetched", albumId);
        return albumMapper.toDtoList(album);
    }

    @Transactional
    public List<AlbumDto> getAllAlbums(AlbumFilterDto filterDto) {
        log.info("User with ID {} is fetching all albums with applied filters", userContext.getUserId());

        Stream<Album> albums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        List<Album> filteredAlbums = filterAlbums(albums, filterDto);

        log.info("Found {} albums after applying filters", filteredAlbums.size());
        return albumMapper.toDtoList(filteredAlbums);
    }

    @Transactional
    public List<AlbumDto> getUserAlbums(AlbumFilterDto filterDto) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is fetching their albums with applied filters", userId);

        Stream<Album> albums = albumRepository.findByAuthorId(userId);
        List<Album> filteredAlbums = filterAlbums(albums, filterDto);

        log.info("Found {} albums after applying filters for user's albums", filteredAlbums.size());
        return albumMapper.toDtoList(filteredAlbums);
    }

    @Transactional
    public List<AlbumDto> getUserFavoriteAlbums(AlbumFilterDto filterDto) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is fetching their favorite albums with filters", userId);

        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userId);
        List<Album> filteredAlbums = filterAlbums(albums, filterDto);

        log.info("Found {} albums after applying filters for user's favorite albums", filteredAlbums.size());
        return albumMapper.toDtoList(filteredAlbums);
    }

    @Transactional
    public AlbumDto updateAlbum(long albumId, AlbumCreateUpdateDto updateDto) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is attempting to update album with ID {}", userId, albumId);

        Album album = getAlbum(albumId);
        validateAlbumAuthor(album, userId, "update album with ID %d".formatted(albumId));
        validateAlbumTitle(updateDto.getTitle(), userId);
        albumMapper.update(updateDto, album);
        album = albumRepository.save(album);

        log.info("User with ID {} successfully updated album with ID {}", userId, albumId);
        return albumMapper.toDtoList(album);
    }

    @Transactional
    public void deleteAlbum(long albumId) {
        long userId = userContext.getUserId();
        log.info("User with ID {} is attempting to delete album with ID {}", userId, albumId);

        Album album = getAlbum(albumId);
        validateAlbumAuthor(album, userId, "delete album with ID %d".formatted(albumId));
        albumRepository.delete(album);

        log.info("User with ID {} successfully deleted album with ID {}", userId, albumId);
    }

    private void validateUserExistence(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UnauthorizedException(userId, e);
        } catch (FeignException e) {
            String errorMessage = "There was an attempt to get %s by ID: %d".formatted(USER, userId);
            throw new FeignClientException(MessageError.FEIGN_CLIENT_UNEXPECTED_EXCEPTION.getMessage(errorMessage), e);
        }
    }

    private void validateAlbumTitle(String title, long userId) {
        if (albumRepository.existsByTitleAndAuthorId(title, userId)) {
            throw new DataValidationException("User with ID %d already has an album titled '%s'.".formatted(userId, title));
        }
    }

    private void validateAlbumAuthor(Album album, long userId, String forbiddenMessage) {
        if (album.getAuthorId() != userId) {
            throw new ForbiddenException(userId, forbiddenMessage);
        }
    }

    private List<Album> filterAlbums(Stream<Album> albums, AlbumFilterDto filterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                        albums,
                        (albumStream, filter) -> filter.apply(albumStream, filterDto),
                        (s1, s2) -> s2
                ).toList();
    }

    private Album getAlbum(long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException(ALBUM, albumId));
    }
}