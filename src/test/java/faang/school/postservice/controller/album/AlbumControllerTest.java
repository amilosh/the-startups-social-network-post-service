package faang.school.postservice.controller.album;

import com.google.gson.Gson;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumRequestUpdateDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    private AlbumController albumController;
    @Mock
    private AlbumService albumService;
    @Mock
    private AlbumValidator validator;
    @Mock
    private UserContext userContext;

    private AlbumResponseDto albumResponseDto;
    private AlbumRequestDto albumRequestDto;
    private AlbumRequestUpdateDto albumRequestUpdateDto;
    private AlbumResponseDto firstAlbum;
    private AlbumResponseDto secondAlbum;
    private Post post;
    private List<AlbumResponseDto> albums;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();

        albumRequestDto = AlbumRequestDto.builder()
                .title("title")
                .description("description")
                .authorId(5L)
                .build();
        albumResponseDto = AlbumResponseDto.builder()
                .id(1L)
                .title("title")
                .postsIds(new ArrayList<>())
                .description("description")
                .authorId(5L)
                .build();
        albumRequestUpdateDto = AlbumRequestUpdateDto.builder()
                .id(56L)
                .description("Описание")
                .title("Заголовок")
                .build();
        post = Post.builder()
                .id(25L)
                .build();
        firstAlbum = AlbumResponseDto.builder()
                .id(3L)
                .title("title1")
                .build();
        secondAlbum = AlbumResponseDto.builder()
                .id(4L)
                .title("title2")
                .build();
        albums = new ArrayList<>(List.of(firstAlbum,secondAlbum));
    }

    @Test
    public void testCreateAlbum() throws Exception {
        when(albumService.createAlbum(albumRequestDto)).thenReturn(albumResponseDto);

        String albumRequestDtoJson = new Gson().toJson(albumRequestDto);

        mockMvc.perform(post("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumRequestDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.authorId", is(5)));
    }

    @Test
    public void testAddPost() throws Exception {
        albumResponseDto.getPostsIds().add(post.getId());
        when(albumService.addPost(1L, 25L)).thenReturn(albumResponseDto);

        mockMvc.perform(post("/api/v1/albums/1/post/25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")));

    }

    @Test
    public void testDeletePost() throws Exception {

        mockMvc.perform(delete("/api/v1/albums/1/post/25"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddAlbumToFavoriteAlbums() throws Exception {
        mockMvc.perform(post("/api/v1/albums/1/favorite"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteAlbumFromFavoriteAlbums() throws Exception {
        mockMvc.perform(delete("/api/v1/albums/1/favorite"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAlbum() throws Exception {
        when(albumService.getAlbum(1L)).thenReturn(albumResponseDto);
        mockMvc.perform(get("/api/v1/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    @Test
    public void testGetAllMyAlbumsByFilter() throws Exception {
        when(albumService.getAllMyAlbumsByFilter(new AlbumFilterDto(),5L)).thenReturn(albums);
        when(userContext.getUserId()).thenReturn(5L);

        mockMvc.perform(get("/api/v1/albums/author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("title1")))
                .andExpect(jsonPath("$[1].title", is("title2")));
    }

    @Test
    public void testGetAllAlbumsByFilter() throws Exception {
        when(albumService.getAllAlbumsByFilter(new AlbumFilterDto())).thenReturn(albums);

        mockMvc.perform(get("/api/v1/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("title1")))
                .andExpect(jsonPath("$[1].title", is("title2")));
    }

    @Test
    public void testGetAllFavoriteAlbumsByFilter() throws Exception {
        when(albumController.getAllFavoriteAlbumsByFilter(new AlbumFilterDto())).thenReturn(albums);

        mockMvc.perform(get("/api/v1/albums/author/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("title1")))
                .andExpect(jsonPath("$[1].title", is("title2")));
    }

    @Test
    public void testUpdateAlbum() throws Exception {
        when(albumService.updateAlbum(albumRequestUpdateDto,5L)).thenReturn(albumResponseDto);
        when(userContext.getUserId()).thenReturn(5L);

        String albumRequestUpdateDtoJson = new Gson().toJson(albumRequestUpdateDto);

        mockMvc.perform(put("/api/v1/albums")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(albumRequestUpdateDtoJson)).andExpect(status().isOk());
    }

    @Test
    public void testDeleteAlbum() throws Exception {
        mockMvc.perform(delete("/api/v1/albums/1"))
                .andExpect(status().isOk());
    }

}
