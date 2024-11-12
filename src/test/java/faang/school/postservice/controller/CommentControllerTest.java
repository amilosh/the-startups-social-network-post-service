package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CreateCommentDto createDto;
    private CommentDto responseDto;
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
        createDto = createTestCreateCommentDto();
        responseDto = createTestCommentDto();
    }

    @Test
    @DisplayName("Create comment successfully")
    void testCreateSuccess() throws Exception {
        when(commentService.create(postId, createDto)).thenReturn(responseDto);

        ResponseEntity<CommentDto> result = commentController.create(postId, createDto);

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.authorId").value(responseDto.getAuthorId()));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1L, result.getBody().getId());
    }

    @Test
    @DisplayName("Create comment fail")
    void testCreateFail() {
        when(commentService.create(postId, createDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentController.create(postId, createDto));
    }

    private CreateCommentDto createTestCreateCommentDto() {
        return CreateCommentDto.builder()
                .authorId(1L)
                .content("Test content")
                .build();
    }

    private CommentDto createTestCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likeIds(null)
                .postId(1L)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}