package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.dto.AlbumUpdateDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.AlbumService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;


    private AlbumDto albumDto;
    private AlbumDto albumDto1;
    private AlbumUpdateDto albumUpdateDto;
    private Long userId = 1L;
    private Long albumId = 2L;
    private Long postId = 3L;
    private PostDto postDto;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();
        albumDto = mockAlbumDto();
        albumDto1 = mockAlbumDto1();
        postDto = mockPostDto();
        albumFilterDto = mockAlbumFilterDto();
        albumUpdateDto = mockAlbumUpdateDto();
    }

    @Test
    @DisplayName("Create album success")
    void testCreateAlbumSuccess() throws Exception {
        when(albumService.createAlbum(albumDto)).thenReturn(albumDto);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.authorId").value(albumDto.getAuthorId()));

        verify(albumService, times(1)).createAlbum(any(AlbumDto.class));
    }

    @Test
    @DisplayName("Create album fail")
    void testCreateAlbumFail() {
        when(albumService.createAlbum(albumDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> albumController.createAlbum(albumDto));
    }

    @Test
    @DisplayName("Add Post To Album success")
    void testAddPostToAlbumSuccess() throws Exception {
        when(albumService.addPostToAlbum(userId, albumId, postId)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/{albumId}/userId/{userId}/posts/{postId}", albumId, userId, postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumDto.getId()));

        verify(albumService, times(1)).addPostToAlbum(userId, albumId, postId);
    }

    @Test
    @DisplayName("Add Post To Album fail")
    void testAddPostToAlbumFail() throws Exception {
        when(albumService.addPostToAlbum(userId, albumId, postId))
                .thenThrow(new EntityNotFoundException("Album not found"));

        assertThrows(EntityNotFoundException.class, () -> albumController.addPostToAlbum(userId, albumId, postId));
    }

    @Test
    @DisplayName("Remove Post From Album success")
    void testRemovePostFromAlbumSuccess() throws Exception {
        when(albumService.removePost(userId, albumId, postId)).thenReturn(albumDto);

        mockMvc.perform(delete("/albums/{albumId}/userId/{userId}/posts/{postId}", albumId, userId, postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()))
                .andExpect(jsonPath("$.description").value(albumDto.getDescription()));

        verify(albumService, times(1)).removePost(userId, albumId, postId);
    }

    @Test
    @DisplayName("Remove Post From Album fail")
    void testRemovePostToAlbumFail() throws Exception {
        when(albumService.removePost(userId, albumId, postId))
                .thenThrow(new EntityNotFoundException("Album not found"));

        assertThrows(EntityNotFoundException.class, () -> albumController.removePostFromAlbum(userId, albumId, postId));
    }

    @Test
    @DisplayName("Adding album to Favorites successfully")
    void testAddAlbumToFavoritesSuccess() throws Exception {
        when(albumService.addAlbumToFavorites(userId, albumId)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/{albumId}/userId/{userId}/favorites",albumId,userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));

        verify(albumService, times(1)).addAlbumToFavorites(userId, albumId);
    }

    @Test
    @DisplayName("Adding album to Favorites fail")
    void testAddAlbumToFavoritesFail() throws Exception {
        when(albumService.addAlbumToFavorites(userId, albumId))
                .thenThrow(new EntityNotFoundException("Album not found"));

        assertThrows(EntityNotFoundException.class, () -> albumController.addAlbumToFavorites(userId, albumId));
    }

    @Test
    @DisplayName("Delete album to Favorites successfully")
    void testDeleteAlbumFromFavoritesSuccess() throws Exception {
        doNothing().when(albumService).deleteAlbumFromFavorites(userId, albumId);

        mockMvc.perform(delete("/albums/{albumId}/userId/{userId}/favorites", albumId,userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(albumService, times(1)).deleteAlbumFromFavorites(userId,albumId);
    }

    @Test
    @DisplayName("Get album by ID success")
    void testGetAlbumByIdSuccess() throws Exception {
        when(albumService.findByAlbumId(albumId)).thenReturn(albumDto);

        mockMvc.perform(get("/albums//{albumId}", albumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()))
                .andExpect(jsonPath("$.description").value(albumDto.getDescription()));

        verify(albumService, times(1)).findByAlbumId(albumId);
    }

    @Test
    @DisplayName("Get album by ID fail")
    void testGetAlbumByIdFail() throws Exception {
        when(albumService.findByAlbumId(albumId))
                .thenThrow(new EntityNotFoundException("Album not found"));

        assertThrows(EntityNotFoundException.class, () -> albumController.getAlbumById(albumId));
    }

    @Test
    @DisplayName("Get usersAlbums with filters success")
    void testGetUsersAlbumsWithFiltersSuccess() throws Exception {
        List<AlbumDto> albums = List.of(albumDto, albumDto1);
        when(albumService.getAlbumsForUserByFilter(userId, albumFilterDto)).thenReturn(albums);
        mockMvc.perform(post("/albums/user/{userId}/albums", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(albums.size()))
                .andExpect(jsonPath("$[0].id").value(albums.get(0).getId()));

        verify(albumService, times(1)).getAlbumsForUserByFilter(userId, albumFilterDto);
    }

    @Test
    @DisplayName("Get usersAlbums with filters when list is empty ")
    void testGetUsersAlbumsWithFiltersEmptyList() throws Exception {
        when(albumService.getAlbumsForUserByFilter(userId, albumFilterDto)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/albums/user/{userId}/albums", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(albumService, times(1)).getAlbumsForUserByFilter(userId, albumFilterDto);
    }

    @Test
    @DisplayName("Get all albums with filters success")
    void testGetAllAlbumsWithFiltersSuccess() throws Exception {
        List<AlbumDto> albums = List.of(albumDto, albumDto1);
        when(albumService.getAllAlbumsByFilter(albumFilterDto)).thenReturn(albums);

        mockMvc.perform(post("/albums/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(albums.size()))
                .andExpect(jsonPath("$[0].id").value(albums.get(0).getId()));

        verify(albumService, times(1)).getAllAlbumsByFilter(albumFilterDto);
    }

    @Test
    @DisplayName("Get all albums with filters when list is empty ")
    void testGetAllAlbumsWithFiltersEmptyList() throws Exception {
        when(albumService.getAllAlbumsByFilter(albumFilterDto)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/albums/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(albumService, times(1)).getAllAlbumsByFilter(albumFilterDto);
    }

    @Test
    @DisplayName("Get favorit usersAlbums with filters success")
    void testGetFavoritUsersAlbumsWithFiltersSuccess() throws Exception {
        List<AlbumDto> albums = List.of(albumDto, albumDto1);
        when(albumService.getFavoritAlbumsForUserByFilter(userId, albumFilterDto)).thenReturn(albums);

        mockMvc.perform(post("/albums/user/{userId}/favorites", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(albums.size()))
                .andExpect(jsonPath("$[0].id").value(albums.get(0).getId()));

        verify(albumService, times(1)).getFavoritAlbumsForUserByFilter(userId, albumFilterDto);
    }

    @Test
    @DisplayName("Get favorit usersAlbums with filters when list is empty ")
    void testGetFavoritUsersAlbumsWithFiltersEmptyList() throws Exception {
        when(albumService.getFavoritAlbumsForUserByFilter(userId, albumFilterDto)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/albums/user/{userId}/favorites", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(albumService, times(1)).getFavoritAlbumsForUserByFilter(userId, albumFilterDto);
    }

    @Test
    @DisplayName("Update album successfully")
    void testUpdateAlbum() throws Exception {
        when(albumService.updateAlbum(albumUpdateDto)).thenReturn(albumDto);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));

        verify(albumService, times(1)).updateAlbum(albumUpdateDto);
    }

    @Test
    @DisplayName("Delete album successfully")
    void testDeleteAlbumSuccess() throws Exception {
        doNothing().when(albumService).deleteAlbum(userId, albumId);

        mockMvc.perform(delete("/albums/{albumId}/userId/{userId}", albumId,userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(albumService, times(1)).deleteAlbum(userId, albumId);
    }

    private AlbumDto mockAlbumDto() {
        return AlbumDto.builder()
                .id(1L)
                .title("Album1")
                .description("album about spring")
                .authorId(1)
                .postsId(List.of(1L, 2L, 3L))
                .build();
    }

    private AlbumDto mockAlbumDto1() {
        return AlbumDto.builder()
                .id(3L)
                .title("Album3")
                .description("album about summer")
                .authorId(2)
                .postsId(List.of(1L, 2L, 3L))
                .build();
    }

    private AlbumUpdateDto mockAlbumUpdateDto() {
        return AlbumUpdateDto.builder()
                .id(1L)
                .title("Album1")
                .description("album about spring")
                .postsId(List.of(1L, 2L, 3L))
                .build();
    }

    private PostDto mockPostDto() {
        postDto = new PostDto();
        postDto.setId(1L);
        return postDto;
    }

    private AlbumFilterDto mockAlbumFilterDto() {
        return AlbumFilterDto.builder()
                .title("Filter")
                .description("Java the best")
                .month(Month.MARCH)
                .build();
    }
}
