package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumValidatorTest {

    @InjectMocks
    private AlbumValidator validator;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private PostRepository postRepository;

    private AlbumRequestDto albumRequestDto;
    private AlbumRequestUpdateDto albumRequestUpdateDto;
    private AlbumResponseDto albumResponseDto;
    private Album album;
    private Post post;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .id(25L)
                .build();
        albumRequestDto = AlbumRequestDto.builder()
                .id(10L)
                .build();
        album = Album.builder()
                .id(10L)
                .posts(new ArrayList<>())
                .build();
        albumResponseDto = AlbumResponseDto.builder()
                .id(10L)
                .authorId(0L)
                .postsIds(new ArrayList<>())
                .build();
        albumRequestUpdateDto = AlbumRequestUpdateDto.builder()
                .id(10L)
                .authorId(5L)
                .postsIds(List.of(25L))
                .build();
    }


    @Test
    public void testValidateAlbumWithTitleNull() {
        albumRequestDto.setTitle(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbum(albumRequestDto));
    }

    @Test
    public void testValidateAlbumWithDescriptionNull() {
        albumRequestDto.setDescription(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbum(albumRequestDto));
    }

    @Test
    public void testValidateAlbumWithAuthorIdNull() {
        albumRequestDto.setAuthorId(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbum(albumRequestDto));
    }

    @Test
    public void testValidateAlbumSuccess() {
        albumRequestDto.setAuthorId(5L);
        albumRequestDto.setDescription("description");
        albumRequestDto.setTitle("title");
        assertDoesNotThrow(() -> validator.validateAlbum(albumRequestDto));
    }

    @Test
    public void testValidateAuthorWithAuthorNotExists() {
        when(userServiceClient.getUser(5L)).thenReturn(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateAuthor(5L));
    }

    @Test
    public void testValidateAuthorSuccess() {
        when(userServiceClient.getUser(5L)).thenReturn(new UserDto());
        assertDoesNotThrow(() -> validator.validateAuthor(5L));
    }

    @Test
    public void testValidateAlbumWithSameTitleExistsWithException() {
        when(albumRepository.existsByTitleAndAuthorId("title", 5L)).thenReturn(true);
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbumWithSameTitleExists(5L, "title"));
    }

    @Test
    public void testValidateAlbumWithSameTitleExistsSuccess() {
        when(albumRepository.existsByTitleAndAuthorId("title", 5L)).thenReturn(false);
        assertDoesNotThrow(() -> validator.validateAlbumWithSameTitleExists(5L, "title"));
    }

    @Test
    public void testValidateAlbumForPostWithNullAuthorId() {
        albumRequestDto.setAuthorId(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbumForPost(albumRequestDto));
    }

    @Test
    public void testValidateAlbumForPostWithNullId() {
        albumRequestDto.setId(null);
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbumForPost(albumRequestDto));
    }

    @Test
    public void testValidateAlbumForPostSuccess() {
        albumRequestDto.setId(1L);
        albumRequestDto.setAuthorId(5L);
        assertDoesNotThrow(() -> validator.validateAlbumForPost(albumRequestDto));
    }

    @Test
    public void testValidatePostWithPostNotExists() {
        when(postRepository.findById(25L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> validator.validatePost(25L));
    }

    @Test
    public void testValidatePostWithPostSuccess() {
        when(postRepository.findById(25L)).thenReturn(Optional.of(post));
        assertDoesNotThrow(() -> validator.validatePost(25L));
    }

    @Test
    public void testValidateAuthorHasThisAlbumWithException() {
        assertThrows(DataValidationException.class,
                () -> validator.validateAuthorHasThisAlbum(album, 5L));
    }

    @Test
    public void testValidateAuthorHasThisAlbumSuccess() {
        album.setAuthorId(5L);
        assertDoesNotThrow(() -> validator.validateAuthorHasThisAlbum(album, 5L));
    }

    @Test
    public void testValidateAlbumExistsWithException() {
        when(albumRepository.findById(13L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class,
                () -> validator.validateAlbumExists(13L));
    }

    @Test
    public void testValidateAlbumExistsSuccess() {
        when(albumRepository.findById(10L)).thenReturn(Optional.of(album));
        assertDoesNotThrow(() -> validator.validateAlbumExists(10L));
    }

    @Test
    public void testValidateFavoritesHasThisAlbum() {}



}
