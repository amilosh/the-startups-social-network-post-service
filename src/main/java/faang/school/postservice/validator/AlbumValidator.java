package faang.school.postservice.validator;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.exceptions.NotUniqueAlbumException;
import faang.school.postservice.repository.AlbumRepository;
import org.springframework.stereotype.Component;

@Component
public class AlbumValidator {
    private AlbumRepository albumRepository;

    public AlbumDto albumExistsByTitleAndAuthorId(AlbumDto albumDto) {
        if (albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())) {
            throw new NotUniqueAlbumException("Album with the same title and author already exists.");
        } else {
            return albumDto;
        }
    }
}
