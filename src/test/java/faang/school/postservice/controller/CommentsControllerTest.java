package faang.school.postservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

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
    void setUp(){
        commentDto= createTestCommentDto();
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
    private ResultActions performPostRequest(String url, CommentDto commentDto) throws Exception{
        return mockMvc.perform(post(url))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto));
    }

    private ResultActions performGetRequest(String url) throws Exception {
        return mockMvc.perform(get(url))
                .
    }
}
