package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.post.response.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createPostShouldReturnCreatedPost() throws Exception {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("Test content");
        createPostDto.setAuthorId(1L);
        createPostDto.setProjectId(1L);

        PostDto postDto = new PostDto();
        postDto.setContent("Test content");

        when(postService.create(any(CreatePostDto.class))).thenReturn(postDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createPostDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test content"));

        verify(postService, times(1)).create(any(CreatePostDto.class));
    }

    @Test
    void publishPostShouldReturnPublishedPost() throws Exception {
        Long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setContent("Test content");
        postDto.setPublished(true);

        when(postService.publish(postId)).thenReturn(postDto);

        mockMvc.perform(put("/posts/{postId}/publish", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.content").value("Test content"));

        verify(postService, times(1)).publish(postId);
    }

    @Test
    void updatePostShouldReturnUpdatedPost() throws Exception {
        Long postId = 1L;
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setContent("Updated content");
        PostDto postDto = new PostDto();
        postDto.setContent("Updated content");

        when(postService.update(postId, updatePostDto)).thenReturn(postDto);

        mockMvc.perform(put("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatePostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));

        verify(postService, times(1)).update(postId, updatePostDto);
    }

    @Test
    void deletePostShouldReturnNoContent() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).delete(postId);

        mockMvc.perform(delete("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).delete(postId);
    }

    @Test
    void getPostByIdShouldReturnPostDto() throws Exception {
        Long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setContent("Post content");

        when(postService.getById(postId)).thenReturn(postDto);

        mockMvc.perform(get("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.content").value("Post content"));

        verify(postService, times(1)).getById(postId);
    }

    @Test
    void getDraftByUserIdShouldReturnPostDtos() throws Exception {
        Long userId = 1L;
        PostDto postDto1 = new PostDto();
        postDto1.setId(1L);
        postDto1.setContent("Draft content 1");

        PostDto postDto2 = new PostDto();
        postDto2.setId(2L);
        postDto2.setContent("Draft content 2");

        List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

        when(postService.getDraftByUserId(userId)).thenReturn(postDtos);

        mockMvc.perform(get("/posts/draft/author/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft content 2"));

        verify(postService, times(1)).getDraftByUserId(userId);
    }

    @Test
    void getDraftByProjectIdShouldReturnPostDtos() throws Exception {
        Long projectId = 1L;
        PostDto postDto1 = new PostDto();
        postDto1.setId(1L);
        postDto1.setContent("Draft content 1");

        PostDto postDto2 = new PostDto();
        postDto2.setId(2L);
        postDto2.setContent("Draft content 2");

        List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

        when(postService.getDraftByProjectId(projectId)).thenReturn(postDtos);

        mockMvc.perform(get("/posts/draft/project/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft content 2"));

        verify(postService, times(1)).getDraftByProjectId(projectId);
    }

    @Test
    void getPublishedByUserIdShouldReturnPostDtos() throws Exception {
        Long userId = 1L;
        PostDto postDto1 = new PostDto();
        postDto1.setId(1L);
        postDto1.setContent("Published content 1");

        PostDto postDto2 = new PostDto();
        postDto2.setId(2L);
        postDto2.setContent("Published content 2");

        List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

        when(postService.getPublishedByUserId(userId)).thenReturn(postDtos);

        mockMvc.perform(get("/posts/published/author/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Published content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Published content 2"));

        verify(postService, times(1)).getPublishedByUserId(userId);
    }

    @Test
    void getPublishedByProjectIdShouldReturnPostDtos() throws Exception {
        Long projectId = 1L;
        PostDto postDto1 = new PostDto();
        postDto1.setId(1L);
        postDto1.setContent("Published content 1");

        PostDto postDto2 = new PostDto();
        postDto2.setId(2L);
        postDto2.setContent("Published content 2");

        List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

        when(postService.getPublishedByProjectId(projectId)).thenReturn(postDtos);

        mockMvc.perform(get("/posts/published/project/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Published content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Published content 2"));

        verify(postService, times(1)).getPublishedByProjectId(projectId);
    }
}