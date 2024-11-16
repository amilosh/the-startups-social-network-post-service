package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class AlbumValidator {

    private UserServiceClient userServiceClient;
    private AlbumRepository albumRepository;
    private PostRepository postRepository;

    public void validateAlbum(AlbumRequestDto albumRequestDto) {
        String title = albumRequestDto.getTitle();
        String description = albumRequestDto.getDescription();
        Long authorId = albumRequestDto.getAuthorId();
        if (title == null || title.isEmpty()) {
            throw new DataValidationException("Title is empty");
        }
        if (description == null || description.isEmpty()) {
            throw new DataValidationException("Description is empty");
        }
        if (authorId == null) {
            throw new DataValidationException("AuthorId is null");
        }
    }

    public void validateAlbum(AlbumRequestUpdateDto albumRequestUpdateDto) {
        String title = albumRequestUpdateDto.getTitle();
        String description = albumRequestUpdateDto.getDescription();
        Long authorId = albumRequestUpdateDto.getAuthorId();
        if (title == null || title.isEmpty()) {
            throw new DataValidationException("Title is empty");
        }
        if (description == null || description.isEmpty()) {
            throw new DataValidationException("Description is empty");
        }
        if (authorId == null) {
            throw new DataValidationException("AuthorId is null");
        }
    }

    public void validateAuthor(long authorId) {
        UserDto author = userServiceClient.getUser(authorId);
        if (author == null) {
            throw new DataValidationException("Author with id " + authorId + " not found");
        }
    }

    public void validateAlbumWithSameTitleExists(long authorId, String title) {
        boolean result = albumRepository.existsByTitleAndAuthorId(title, authorId);
        if (result) {
            throw new DataValidationException("Title " + title + " already exists");
        }
    }

    public void validateAlbumForAddPost(AlbumRequestDto albumRequestDto) {
        Long authorId = albumRequestDto.getAuthorId();
        Long id = albumRequestDto.getId();
        if (authorId == null) {
            throw new DataValidationException("AuthorId is null");
        }
        if (id == null) {
            throw new DataValidationException("Id is null");
        }
    }

    public Post validatePost(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new DataValidationException("Post with id " + postId + " not found");
        }
        return post.get();
    }

    public void validateAuthorHasThisAlbum(Album album, long authorId) {
        if (authorId != album.getAuthorId()) {
            throw new DataValidationException("AuthorId " + authorId +
                    " is not the same as albumsAuthorId " + album.getAuthorId());
        }
    }

    public Album validateAlbumExists(long id) {
        Optional<Album> album = albumRepository.findById(id);
        if (album.isEmpty()) {
            throw new DataValidationException("Album with id " + id + " not found");
        }
        return album.get();
    }

    public boolean validateFavoritesHasThisAlbum(long albumId, long authorId) {
        Stream<Album> albums = albumRepository.findFavoriteAlbumsByUserId(authorId);
        return albums.anyMatch(album -> albumId == album.getId());
    }

}
