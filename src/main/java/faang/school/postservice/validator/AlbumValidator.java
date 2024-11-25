package faang.school.postservice.validator;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.exception.NotUniqueAlbumException;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumValidator {
    private final AlbumRepository albumRepository;

    public AlbumDto albumExistsByTitleAndAuthorId(AlbumDto albumDto) {
        if (albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())) {
            throw new NotUniqueAlbumException("Album with the same title and author already exists.");
        } else {
            return albumDto;
        }
    }
}
