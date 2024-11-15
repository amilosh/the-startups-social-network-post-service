package faang.school.postservice.service;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.AlbumUpdateDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exceptions.EntityNotFoundException;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
public class AlbumService {

    private AlbumRepository albumRepository;

    private PostValidator postValidator;

    private UserValidator userValidator;

    private AlbumValidator albumValidator;

    private AlbumMapper albumMapper;

    private PostMapper postMapper;

    private List<Filter<Album, AlbumFilterDto>> filters;


    public AlbumDto createAlbum(AlbumDto albumDto) {
        userValidator.checkUserExistence(albumDto);
        Album album = albumMapper.toEntity(albumValidator.albumExistsByTitleAndAuthorId(albumDto));
        Album saveAlbum = albumRepository.save(album);
        return albumMapper.toDto(saveAlbum);
    }

    public AlbumDto addPostToAlboom(long userId, long albumId, PostDto postDto) {
        postValidator.checkPostExistence(postDto);
        Album album = findAlbumForUser(userId, albumId);
        album.addPost(postMapper.toEntity(postDto));
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    public AlbumDto removePost(long userId, long albumId, PostDto postDto) {
        postValidator.checkPostExistence(postDto);
        Album album = findAlbumForUser(userId, albumId);
        album.removePost(postDto.getId());
        albumRepository.save(album);
        return albumMapper.toDto(album);
    }

    public AlbumDto addAlbumToFavorites(long userId, AlbumDto albumDto) {
        AlbumDto existAlbum = findByAlbumId(albumDto.getId());
        albumRepository.addAlbumToFavorites(existAlbum.getId(), userId);
        return albumDto;
    }

    public void deleteAlbumFromFavorites(long userId, AlbumDto albumDto) {
        findByAlbumId(albumDto.getId());
        albumRepository.deleteAlbumFromFavorites(userId, albumDto.getAuthorId());
    }

    public AlbumDto findByAlbumId(long albumId) {
        return albumRepository.findById(albumId)
                .map(albumMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Album whith id " + albumId + " not found"));
    }

    public List<AlbumDto> getAlbumsForUserByFilter(long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = albumRepository.findByAuthorId(authorId);
        List<AlbumDto> result = filters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1)
                .map(albumMapper::toDto)
                .toList();
        log.info("Albums filtered by {}.", albumFilterDto);
        return result;
    }

    public List<AlbumDto> getAllAlbumsByFilter(AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = StreamSupport.stream(albumRepository.findAll().spliterator(), false);
        List<AlbumDto> result = filters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1)
                .map(albumMapper::toDto)
                .toList();
        log.info("All albums filtered by {}.", albumFilterDto);
        return result;
    }

    public List<AlbumDto> getFavoritAlbumsForUserByFilter(long authorId, AlbumFilterDto albumFilterDto) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        List<AlbumDto> result = filters.stream()
                .filter(filter -> filter.isApplicable(albumFilterDto))
                .reduce(albums,
                        (stream, filter) -> filter.apply(stream, albumFilterDto),
                        (s1, s2) -> s1)
                .map(albumMapper::toDto)
                .toList();
        log.info("Albums filtered by {}.", albumFilterDto);
        return result;
    }

    public AlbumDto updateAlbum(AlbumUpdateDto albumUpdateDto) {
        Album updateAlbum = albumMapper.toEntity(findByAlbumId(albumUpdateDto.getId()));
        albumMapper.update(albumUpdateDto, updateAlbum);
        return albumMapper.toDto(albumRepository.save(updateAlbum));
    }

    public void deleteAlbum(long userId, AlbumDto albumDto) {
        Album albumForUser = findAlbumForUser(userId, albumDto.getId());
        albumRepository.deleteById(albumDto.getId());
    }


    private Album findAlbumForUser(long userId, long albumId) {
        return albumRepository.findByAuthorId(userId)
                .filter(album -> album.getId() == (albumId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Album not found or doesn't belong to the user "));
    }
}
