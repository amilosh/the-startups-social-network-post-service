package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PostService postService;
    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new PostController(postService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreatePost() throws Exception {
        PostRequestDto requestDto = new PostRequestDto();
        PostResponseDto responseDto = new PostResponseDto();

        when(postService.create(any(PostRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(postService).create(any(PostRequestDto.class));
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
    void testUpdatePost() throws Exception {
        PostUpdateDto updateDto = new PostUpdateDto();
        PostResponseDto responseDto = new PostResponseDto();

        when(postService.updatePost(any(PostUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(postService).updatePost(any(PostUpdateDto.class));
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
    void testGetPostById() throws Exception {
        Long postId = 1L;
        PostResponseDto responseDto = new PostResponseDto();

        when(postService.getPostById(postId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(postService).getPostById(postId);
    }

    @Test
    void testGetPosts() throws Exception {
        PostFilterDto filterDto = new PostFilterDto();
        List<PostResponseDto> responseDtos = List.of(new PostResponseDto());

        when(postService.getPosts(any(PostFilterDto.class))).thenReturn(responseDtos);

        mockMvc.perform(get("/api/v1/posts")

                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDtos)));

        verify(postService).getPosts(any(PostFilterDto.class));
    }
}