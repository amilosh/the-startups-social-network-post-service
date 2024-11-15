package faang.school.postservice.service.post.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.FeignClientException;
import faang.school.postservice.exception.MessageError;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.zip.DataFormatException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private static final String USER = "User";

    private final UserServiceClient userServiceClient;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    public AlbumDto createAlbum(long authorId, AlbumCreateDto createDto) {
        validateAuthorId(authorId);
        validateAuthorTitle(createDto.getTitle(), authorId);
        Album albumToSave = albumMapper.toEntity(createDto);
        Album savedAlbum = albumRepository.save(albumToSave);
        return albumMapper.toDto(savedAlbum);
    }



    private void validateAuthorId(long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch(FeignException.NotFound e) {
            throw new EntityNotFoundException(USER, authorId);
        } catch (Exception e) {
            throw new FeignClientException(
                    MessageError.FEIGN_CLIENT_UNEXPECTED_EXCEPTION
                            .getMessage("There was an attempt to get %s by ID: %d".formatted(USER, authorId)),
                    e
            );
        }
    }

    private void validateAuthorTitle(String title, long authorId) {
        if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
            throw new DataValidationException("User with ID %d already has an album titled '%s'.".formatted(authorId, title));
        }
    }
}
