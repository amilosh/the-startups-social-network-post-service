package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.utilities.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    private final static String mainUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.COMMENT;

    private MockMvc mockMvc;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();


    private final long commentIdFirst = 1L;
    private final long commentIdSecond = 2L;

    @Test
    public void createComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentIdFirst);
        CommentDto commentDtoExpected = new CommentDto();
        commentDtoExpected.setId(commentIdSecond);


        when(commentService.createComment(any(CommentDto.class))).thenReturn(commentDtoExpected);

        mockMvc.perform(post(mainUrl)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
//                .andDo(print())
//                .content(cc))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

    }


    @Test
    public void getAllCommentsSuccessTest() throws Exception {
        long postId = 3L;

        CommentDto commentDtoFirst = new CommentDto();
        commentDtoFirst.setId(commentIdFirst);
        CommentDto commentDtoSecond = new CommentDto();
        commentDtoSecond.setId(commentIdSecond);

        List<CommentDto> expectedComments = List.of(
                commentDtoFirst,
                commentDtoSecond
        );

        when(commentService.getAllComments(postId)).thenReturn(expectedComments);

        mockMvc.perform(get(mainUrl + "/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
    }

    @Test
    public void deleteCommentSuccessTest() throws Exception {
//        long commentId = 1L;
//        doNothing().when(recommendationValidatorMock).validateContent(stringArgumentCaptor.capture());
//        when(commentService.deleteComment(commentId));

        mockMvc.perform(get(mainUrl + "/" + commentIdFirst))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

}
