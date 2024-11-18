package faang.school.postservice.controller.album;

import com.google.gson.Gson;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumRequestDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();

        albumRequestDto = AlbumRequestDto.builder()
                .id(1L)
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


}
