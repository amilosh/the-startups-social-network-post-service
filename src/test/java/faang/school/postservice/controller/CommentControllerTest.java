package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.RequestCommentUpdateDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private static final long COMMENT_ID_ONE = 1;
    private static final long COMMENT_ID_TWO = 2;
    private static final long POST_ID_TWENTY_ONE = 21;
    private static final String COMMENT_TEXT = "This is a comment";
    private static final long AUTHOR_ID_THIRTY_0NE = 31L;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        Post postOne = new Post();
        postOne.setId(1L);
        Post postTwo = new Post();
        postTwo.setId(2L);
    }

    @Test
    public void testCreate() throws Exception {
        RequestCommentDto input = new RequestCommentDto();
        input.setContent(COMMENT_TEXT);
        input.setPostId(POST_ID_TWENTY_ONE);
        input.setAuthorId(AUTHOR_ID_THIRTY_0NE);

        ResponseCommentDto expectedOutput = new ResponseCommentDto();
        expectedOutput.setId(COMMENT_ID_ONE);
        expectedOutput.setPostId(POST_ID_TWENTY_ONE);

        when(commentService.createComment(input)).thenReturn(expectedOutput);

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) COMMENT_ID_ONE)))
                .andExpect(jsonPath("$.postId", is((int) POST_ID_TWENTY_ONE)));

        verify(commentService).createComment(input);
        verifyNoMoreInteractions(commentService);
    }

    @Test
    public void testUpdateComment() throws Exception {
        RequestCommentUpdateDto updateDto = new RequestCommentUpdateDto();
        updateDto.setCommentId(COMMENT_ID_ONE);
        updateDto.setContent(COMMENT_TEXT);

        ResponseCommentDto expectedOutput = new ResponseCommentDto();
        expectedOutput.setId(COMMENT_ID_ONE);
        expectedOutput.setContent(COMMENT_TEXT);

        when(commentService.updateComment(updateDto)).thenReturn(expectedOutput);

        mockMvc.perform(put("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) COMMENT_ID_ONE)))
                .andExpect(jsonPath("$.content", is(COMMENT_TEXT)));
    }


    @Test
    public void testGetCommentsByPostId() throws Exception {
        ResponseCommentDto responseCommentDtoOne = new ResponseCommentDto();
        responseCommentDtoOne.setId(COMMENT_ID_ONE);
        responseCommentDtoOne.setPostId(POST_ID_TWENTY_ONE);

        ResponseCommentDto responseCommentDtoTwo = new ResponseCommentDto();
        responseCommentDtoTwo.setId(COMMENT_ID_TWO);
        responseCommentDtoTwo.setPostId(POST_ID_TWENTY_ONE);

        List<ResponseCommentDto> expectedComments = List.of(responseCommentDtoOne, responseCommentDtoTwo);

        when(commentService.getCommentsByPostId(POST_ID_TWENTY_ONE)).thenReturn(expectedComments);

        mockMvc.perform(get("/api/v1/comments")
                        .param("postId", String.valueOf(POST_ID_TWENTY_ONE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is((int) COMMENT_ID_ONE)))
                .andExpect(jsonPath("$[0].postId", is((int) POST_ID_TWENTY_ONE)))
                .andExpect(jsonPath("$[1].id", is((int) COMMENT_ID_TWO)))
                .andExpect(jsonPath("$[1].postId", is((int) POST_ID_TWENTY_ONE)));
    }

    @Test
    public void testDeleteComment() throws Exception {
        long commentId = COMMENT_ID_ONE;

        mockMvc.perform(delete("/api/v1/comments/{commentId}", commentId))
                .andExpect(status().isOk());

        verify(commentService).deleteComment(commentId);
        verifyNoMoreInteractions(commentService);
    }
}