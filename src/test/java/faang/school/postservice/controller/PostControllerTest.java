package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.handler.ExceptionApiHandler;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utilities.UrlUtils;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import({PostController.class, ExceptionApiHandler.class})
class PostControllerTest {

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createDraftPostByUserSuccessTest() throws Exception {
        PostDto postDto = new PostDto("Test content", 1L, null);
        Long expectedPostId = 1L;
        when(postService.createDraftPost(postDto)).thenReturn(1L);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(expectedPostId));

        verify(postService).createDraftPost(postDto);
    }

    @Test
    void createDraftPostByProjectSuccessTest() throws Exception {
        PostDto postDto = new PostDto("Test content", null, 3L);
        Long expectedPostId = 2L;
        when(postService.createDraftPost(postDto)).thenReturn(2L);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(expectedPostId));

        verify(postService).createDraftPost(postDto);
    }

    @Test
    void createDraftPostByUserContentIsBlankFailTest() throws Exception {
        PostDto postDto = new PostDto("  ", 1L, null);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CoreMatchers.containsString("Content should not be blank")));
    }

    @Test
    void createDraftPostByUserContentNullFailTest() throws Exception {
        PostDto postDto = new PostDto(null, 1L, null);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(CoreMatchers.containsString("Content should not be blank")));
    }

    @Test
    void createDraftPostByUserIdNullAndProjectIdNullFailTest() throws Exception {
        PostDto postDto = new PostDto("Test for test", null, null);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'message':'idProject or idUser are NULL'}"));
    }

    @Test
    void createDraftPostByUserIdAndProjectIdFailTest() throws Exception {
        PostDto postDto = new PostDto("Test for test", 1L, 2L);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'message':'idProject or idUser both has value'}"));
    }

    @Test
    void publishPostSuccessTest() throws Exception {
        PostDto postDto = new PostDto("Test content", 1L, null);
        Long postId = 1L;

        when(postService.publishPost(postId)).thenReturn(postDto);
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.ID, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(postService).publishPost(postId);
    }

    @Test
    void publishPostIsPublishedFailTest() throws Exception {
        Long postId = 1L;
        String errorMessage = "Post already published, id: " + postId;

        when(postService.publishPost(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.ID, postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void publishPostNotFoundFailTest() throws Exception {
        long invalidPostId = 999L;
        String errorMessage = "Post not found with ID: " + invalidPostId;

        when(postService.publishPost(anyLong())).thenThrow(new EntityNotFoundException(errorMessage));
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.ID, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void deletePostSuccessTest() throws Exception {
        Long postId = 1L;
        when(postService.deletePost(postId)).thenReturn(postId);

        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.ID, postId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(postId)));

        verify(postService).deletePost(postId);
    }

    @Test
    void deletePostIsDeletedFailTest() throws Exception {
        Long postId = 1L;
        String errorMessage = "Post with id: " + postId + " was deleted";

        when(postService.deletePost(anyLong())).thenThrow(new IllegalArgumentException("Post with id: " + postId + " was deleted"));
        mockMvc.perform(delete(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.ID, postId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(postService).deletePost(postId);
    }

    @Test
    void getPostSuccessTest() throws Exception {

        PostDto postDto = new PostDto("Sample Post", 1L, 2L);
        when(postService.getPost(anyLong())).thenReturn(postDto);

        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
                        .param("postId", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Sample Post"))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(postService).getPost(1L);
    }

    @Test
    void getPostNotFoundFailTest() throws Exception {
        long invalidPostId = 999L;
        String errorMessage = "Post not found with ID: " + invalidPostId;

        when(postService.publishPost(anyLong())).thenThrow(new EntityNotFoundException(errorMessage));
        mockMvc.perform(patch(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.ID, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getDraftPostsByUserIdSuccessTest() throws Exception {
        Long userId = 1L;
        List<PostDto> drafts = List.of(
                new PostDto("Draft 1", 1L, null),
                new PostDto("Draft 2", 2L, null)
        );
        when(postService.getDraftPostsForUser(userId)).thenReturn(drafts);

        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.USER + UrlUtils.DRAFT, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(drafts.size()))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft 1"))
                .andExpect(jsonPath("$[1].userId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft 2"));

        verify(postService).getDraftPostsForUser(userId);
    }

    @Test
    void getDraftPostsByUserBadUserIdFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.USER + UrlUtils.DRAFT, 0)
                        .param("idUser", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDraftPostsByUserIdUserNotExistFailTest() throws Exception {
        Long userId = 99L;
        String errorMessage = "User id: " + userId + " not found";

        when(postService.getDraftPostsForUser(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.USER + UrlUtils.DRAFT, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getDraftPostsByProjectIdSuccessTest() throws Exception {
        Long projectId = 1L;
        List<PostDto> drafts = List.of(
                new PostDto("Draft 1", null, 1L),
                new PostDto("Draft 2", null, 2L)
        );
        when(postService.getDraftPostsForProject(projectId)).thenReturn(drafts);

        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.PROJECT + UrlUtils.DRAFT, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(drafts.size()))
                .andExpect(jsonPath("$[0].projectId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft 1"))
                .andExpect(jsonPath("$[1].projectId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft 2"));

        verify(postService).getDraftPostsForProject(projectId);
    }

    @Test
    void getDraftPostsByProjectBadProjectIdFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.PROJECT + UrlUtils.DRAFT, 0)
                        .param("idProject", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDraftPostsByProjectIdProjectNotExistFailTest() throws Exception {
        Long projectId = 99L;
        String errorMessage = "Project id: " + projectId + " not found";

        when(postService.getDraftPostsForProject(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.PROJECT + UrlUtils.DRAFT, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getPublishedPostsByUserIdSuccessTest() throws Exception {
        Long userId = 1L;
        List<PostDto> drafts = List.of(
                new PostDto("Publish 1", 1L, null),
                new PostDto("Publish 2", 2L, null)
        );
        when(postService.getPublishedPostsForUser(userId)).thenReturn(drafts);

        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.USER + UrlUtils.PUBLISHED, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(drafts.size()))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Publish 1"))
                .andExpect(jsonPath("$[1].userId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Publish 2"));

        verify(postService).getPublishedPostsForUser(userId);
    }

    @Test
    void getPublishedPostsByUserBadUserIdFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.USER + UrlUtils.PUBLISHED, 0)
                        .param("idUser", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPublishedPostsByUserIdUserNotExistFailTest() throws Exception {
        Long userId = 99L;
        String errorMessage = "User id: " + userId + " not found";

        when(postService.getPublishedPostsForUser(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.USER + UrlUtils.PUBLISHED, userId)
                        .param("idUser", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getPublishedPostsByProjectIdSuccessTest() throws Exception {
        Long projectId = 1L;
        List<PostDto> publishedPosts = List.of(
                new PostDto("Post 1", null, 1L),
                new PostDto("Post 2", null, 2L)
        );
        when(postService.getPublishedPostForProject(projectId)).thenReturn(publishedPosts);

        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.PROJECT + UrlUtils.PUBLISHED, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(publishedPosts.size()))
                .andExpect(jsonPath("$[0].projectId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Post 1"))
                .andExpect(jsonPath("$[1].projectId").value(2L))
                .andExpect(jsonPath("$[1].content").value("Post 2"));

        verify(postService).getPublishedPostForProject(projectId);
    }

    @Test
    void getPublishedPostsByProjectBadProjectIdFailTest() throws Exception {
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.PROJECT + UrlUtils.PUBLISHED, 0)
                        .param("idProject", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPublishedPostsByProjectIdProjectNotExistFailTest() throws Exception {
        Long projectId = 99L;
        String errorMessage = "Project id: " + projectId + " not found";

        when(postService.getPublishedPostForProject(anyLong())).thenThrow(new IllegalArgumentException(errorMessage));
        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST + UrlUtils.PROJECT + UrlUtils.PUBLISHED, projectId)
                        .param("idProject", String.valueOf(projectId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}