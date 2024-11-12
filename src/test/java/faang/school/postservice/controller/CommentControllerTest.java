package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.model.Comment;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    private UpdateCommentDto updateDto;
    private Comment comment;
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
        createDto = mockCreateCommentDto();
        updateDto = mockUpdateCommentDto();
        responseDto = mockCommentDto();
        comment = mockComment();
    }

    @Test
    @DisplayName("Create comment successfully")
    void testCreateCommentSuccess() throws Exception {
        when(commentService.createComment(postId, createDto)).thenReturn(responseDto);

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
    void testCreateCommentFail() {
        when(commentService.createComment(postId, createDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentController.create(postId, createDto));
    }

    @Test
    @DisplayName("Update comment success")
    void testUpdateCommentSuccess() throws Exception {
        System.out.println(responseDto);
        when(commentService.updateComment(postId, updateDto)).thenReturn(responseDto);

        CommentDto result = commentController.update(postId, updateDto);

        mockMvc.perform(put("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()))
                .andExpect(jsonPath("$.authorId").value(responseDto.getAuthorId()));

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Update comment fail")
    void testUpdateCommentFail() {
        when(commentService.updateComment(postId, updateDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentController.update(postId, updateDto));
    }

    @Test
    @DisplayName("Get all comments successfully")
    void testGetAllCommentsSuccess() throws Exception {
        List<CommentDto> comments = List.of(responseDto, responseDto, responseDto);
        when(commentService.getAllComments(postId)).thenReturn(comments);

        mockMvc.perform(get("/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(comments.get(0).getId()))
                .andExpect(jsonPath("$[2].authorId").value(comments.get(2).getAuthorId()));

        assertNotNull(commentService.getAllComments(postId));
        assertEquals(3, commentService.getAllComments(postId).size());
    }

    @Test
    @DisplayName("Get all comments fail")
    void testGetAllComments() {
        when(commentService.getAllComments(postId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllComments(postId));
    }

    private CreateCommentDto mockCreateCommentDto() {
        return CreateCommentDto.builder()
                .authorId(1L)
                .content("Test content")
                .build();
    }

    private UpdateCommentDto mockUpdateCommentDto() {
        return UpdateCommentDto.builder()
                .id(3L)
                .authorId(1L)
                .content("Test update content")
                .build();
    }

    private CommentDto mockCommentDto() {
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

    private Comment mockComment() {
        return Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likes(null)
                .post(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}