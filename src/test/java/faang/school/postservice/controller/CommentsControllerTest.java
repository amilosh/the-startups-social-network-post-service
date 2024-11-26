package faang.school.postservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = createTestCommentDto();
    }

    private CommentDto createTestCommentDto() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setContent("Test Comment");
        dto.setAuthorId(100L);
        dto.setPostId(5L);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    void createComment_Success() throws Exception {
        when(commentService.createComment(eq(5L), any(CommentDto.class))).thenReturn(commentDto);

        performPostRequest("/comments/post/5/comments", commentDto)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    void updateComment_Success() throws Exception {
        when(commentService.updateComment(eq(1L), any(CommentDto.class))).thenReturn(commentDto);

        performPutRequest("/comments/1", commentDto)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    void getAllComments_Success() throws Exception {
        when(commentService.getAllComments(5L)).thenReturn(List.of(commentDto));

        performGetRequest("/posts/5/comments")
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(commentDto))));
    }

    @Test
    void deleteComment_Success() throws Exception {
        performDeleteRequest("/comments/1")
                .andExpect(status().isOk())
                .andExpect(content().string("Comment is deleted successfully"));
    }

    private ResultActions performPostRequest(String url, CommentDto commentDto) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)));
    }

    private ResultActions performGetRequest(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performDeleteRequest(String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    private ResultActions performPutRequest(String url, CommentDto commentDto) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)));
    }
}