package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private ResourceResponseDto resourceResponseDtoForImageOne;
    private ResourceResponseDto resourceResponseDtoForImageTwo;
    private ResourceResponseDto resourceResponseDtoForAudio;

    private ResourceResponseDto resourceResponseDtoForImageUpdate;
    private ResourceResponseDto resourceResponseDtoForAudioUpdate;


    private static final MockMultipartFile IMAGE_FILE_ONE =
            new MockMultipartFile("imageFile1", "image1.jpg", "image/jpeg", "image data".getBytes());
    private static final MockMultipartFile IMAGE_FILE_TWO =
            new MockMultipartFile("imageFile2", "image2.jpg", "image/jpeg", "image data".getBytes());
    private static final MockMultipartFile AUDIO_FILE =
            new MockMultipartFile("audioFile1", "audio1.mp3", "audio/mpeg", "audio data".getBytes());

    private static final MockMultipartFile IMAGE_FOR_UPDATE =
            new MockMultipartFile("images", "newImage.jpg", "image/jpeg", "new image data".getBytes());
    private static final MockMultipartFile AUDIO_FOR_UPDATE =
            new MockMultipartFile("audio", "newAudio.mp3", "audio/mpeg", "new audio data".getBytes());

    private static final long VALID_AUTHOR_ID = 1L;
    private static final long VALID_PROJECT_ID = 20L;
    private static final long VALID_POST_ID = 300L;
    private static final String CONTENT = "some content";

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        objectMapper = new ObjectMapper();

        resourceResponseDtoForImageOne = new ResourceResponseDto();
        resourceResponseDtoForImageOne.setId(1L);
        resourceResponseDtoForImageOne.setType("image");
        resourceResponseDtoForImageOne.setPostId(VALID_POST_ID);
        resourceResponseDtoForImageOne.setDownloadUrl("https://example.com/download/image1");

        resourceResponseDtoForImageTwo = new ResourceResponseDto();
        resourceResponseDtoForImageTwo.setId(2L);
        resourceResponseDtoForImageTwo.setType("image");
        resourceResponseDtoForImageTwo.setPostId(VALID_POST_ID);
        resourceResponseDtoForImageTwo.setDownloadUrl("https://example.com/download/image2");

        resourceResponseDtoForAudio = new ResourceResponseDto();
        resourceResponseDtoForAudio.setId(3L);
        resourceResponseDtoForAudio.setType("audio");
        resourceResponseDtoForAudio.setPostId(VALID_POST_ID);
        resourceResponseDtoForAudio.setDownloadUrl("https://example.com/download/audio1");

        resourceResponseDtoForImageUpdate = new ResourceResponseDto();
        resourceResponseDtoForImageUpdate.setId(4L);
        resourceResponseDtoForImageUpdate.setType("image");
        resourceResponseDtoForImageUpdate.setPostId(VALID_POST_ID);
        resourceResponseDtoForImageUpdate.setDownloadUrl("https://example.com/download/newImage");

        resourceResponseDtoForAudioUpdate = new ResourceResponseDto();
        resourceResponseDtoForAudioUpdate.setId(5L);
        resourceResponseDtoForAudioUpdate.setType("audio");
        resourceResponseDtoForAudioUpdate.setPostId(VALID_POST_ID);
        resourceResponseDtoForAudioUpdate.setDownloadUrl("https://example.com/download/newAudio");
    }

    @Test
    public void shouldCreatePostSuccessfully() throws Exception {
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(VALID_POST_ID)
                .content(CONTENT)
                .authorId(VALID_AUTHOR_ID)
                .published(false)
                .projectId(VALID_PROJECT_ID)
                .images(List.of(resourceResponseDtoForImageOne, resourceResponseDtoForImageTwo))
                .audio(List.of(resourceResponseDtoForAudio))
                .build();

        when(postService.create(any(PostRequestDto.class))).thenReturn(responseDto);

        String postDtoJson = new ObjectMapper().writeValueAsString(
                PostRequestDto.builder()
                        .authorId(1L)
                        .projectId(100L)
                        .content("Some content here")
                        .build()
        );

        MockMultipartFile postDtoPart = new MockMultipartFile(
                "postDto",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                postDtoJson.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts")
                        .file(postDtoPart)
                        .file(IMAGE_FILE_ONE)
                        .file(IMAGE_FILE_TWO)
                        .file(AUDIO_FILE)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        PostResponseDto actualResponse = new ObjectMapper().readValue(jsonResponse, PostResponseDto.class);

        assertEquals(VALID_POST_ID, actualResponse.getId());
        assertEquals(CONTENT, actualResponse.getContent());
        assertEquals(VALID_AUTHOR_ID, actualResponse.getAuthorId());
        assertEquals(VALID_PROJECT_ID, actualResponse.getProjectId());
        assertEquals(2, actualResponse.getImages().size());
        assertEquals(1, actualResponse.getAudio().size());

        ResourceResponseDto image1 = actualResponse.getImages().get(0);
        assertEquals(1L, image1.getId());
        assertEquals("image", image1.getType());
        assertEquals(VALID_POST_ID, image1.getPostId());
        assertEquals("https://example.com/download/image1", image1.getDownloadUrl());

        ResourceResponseDto audio1 = actualResponse.getAudio().get(0);
        assertEquals(3L, audio1.getId());
        assertEquals("audio", audio1.getType());
        assertEquals(VALID_POST_ID, audio1.getPostId());
        assertEquals("https://example.com/download/audio1", audio1.getDownloadUrl());

        verify(postService, times(1)).create(any(PostRequestDto.class));
    }

    @Test
    void testPublishPost() throws Exception {
        Long postId = 1L;
        PostResponseDto responseDto = new PostResponseDto();

        when(postService.publishPost(postId)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/posts/{id}/publish", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(postService).publishPost(postId);
    }

    @Test
    public void shouldUpdatePostSuccessfully() throws Exception {
        PostResponseDto updatedResponse = PostResponseDto.builder()
                .id(VALID_POST_ID)
                .content("Updated Content")
                .authorId(VALID_AUTHOR_ID)
                .published(true)
                .projectId(VALID_PROJECT_ID)
                .images(List.of(resourceResponseDtoForImageUpdate))
                .audio(List.of(resourceResponseDtoForAudioUpdate))
                .build();

        when(postService.updatePost(eq(1L), any(PostUpdateDto.class))).thenReturn(updatedResponse);

        String postDtoJson = new ObjectMapper().writeValueAsString(
                PostUpdateDto.builder()
                        .content("Updated Content")
                        .build()
        );

        MockMultipartFile postDtoPart = new MockMultipartFile(
                "postDto",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                postDtoJson.getBytes(StandardCharsets.UTF_8)
        );


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/v1/posts/1")
                        .file(postDtoPart)
                        .file(IMAGE_FOR_UPDATE)
                        .file(AUDIO_FOR_UPDATE)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        PostResponseDto actualResponse = new ObjectMapper().readValue(jsonResponse, PostResponseDto.class);

        assertEquals(VALID_POST_ID, actualResponse.getId());
        assertEquals("Updated Content", actualResponse.getContent());
        assertTrue(actualResponse.isPublished());
        assertEquals(VALID_PROJECT_ID, actualResponse.getProjectId());

        ResourceResponseDto image = actualResponse.getImages().get(0);
        assertEquals(4L, image.getId());
        assertEquals("image", image.getType());
        assertEquals("https://example.com/download/newImage", image.getDownloadUrl());

        ResourceResponseDto audio = actualResponse.getAudio().get(0);
        assertEquals(5L, audio.getId());
        assertEquals("audio", audio.getType());
        assertEquals("https://example.com/download/newAudio", audio.getDownloadUrl());

        verify(postService, times(1)).updatePost(eq(1L), any(PostUpdateDto.class));
    }

    @Test
    void testDeletePost() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).deletePost(postId);

        mockMvc.perform(delete("/api/v1/posts/{id}", postId))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(postId);
    }

    @Test
    public void shouldReturnPostByIdSuccessfully() throws Exception {
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(VALID_POST_ID)
                .content(CONTENT)
                .authorId(VALID_AUTHOR_ID)
                .published(false)
                .projectId(VALID_PROJECT_ID)
                .images(List.of(resourceResponseDtoForImageOne, resourceResponseDtoForImageTwo))
                .audio(List.of(resourceResponseDtoForAudio))
                .build();

        when(postService.getPost(1L)).thenReturn(responseDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/posts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        PostResponseDto actualResponse = new ObjectMapper().readValue(jsonResponse, PostResponseDto.class);

        assertEquals(VALID_POST_ID, actualResponse.getId());
        assertEquals("some content", actualResponse.getContent());
        assertEquals(VALID_AUTHOR_ID, actualResponse.getAuthorId());
        assertEquals(VALID_PROJECT_ID, actualResponse.getProjectId());
        assertFalse(actualResponse.isPublished());


        ResourceResponseDto image = actualResponse.getImages().get(0);
        assertEquals(1L, image.getId());
        assertEquals("image", image.getType());
        assertEquals("https://example.com/download/image1", image.getDownloadUrl());

        ResourceResponseDto audio = actualResponse.getAudio().get(0);
        assertEquals(3L, audio.getId());
        assertEquals("audio", audio.getType());
        assertEquals("https://example.com/download/audio1", audio.getDownloadUrl());

        verify(postService, times(1)).getPost(1L);
    }

    @Test
    void testGetPosts() throws Exception {
        PostFilterDto filterDto = new PostFilterDto();
        List<PostResponseDto> responseDtos = List.of(new PostResponseDto());

        when(postService.getPosts(any(PostFilterDto.class))).thenReturn(responseDtos);

        mockMvc.perform(get("/api/v1/posts")

                        .param("category", "tech")
                        .param("author", "John")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDtos)));

        verify(postService).getPosts(any(PostFilterDto.class));
    }
}