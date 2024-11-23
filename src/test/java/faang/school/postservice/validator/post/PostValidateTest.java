package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.excaption.post.PostException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class PostValidateTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    void shouldValidateUserExist() {

        Long userId = 1L;

        postValidator.validateUserExist(userId);

        verify(userServiceClient).getUser(userId);
    }

    @Test
    void shouldValidateProjectExist() {

        Long projectId = 1L;

        postValidator.validateProjectExist(projectId);

        verify(projectServiceClient).getProject(projectId);
    }

    @Test
    void shouldValidateCreateWithAuthorId() {

        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);

        postValidator.validateCreate(postDto);

        verify(userServiceClient).getUser(postDto.getAuthorId());
        verifyNoInteractions(projectServiceClient);
    }

    @Test
    void shouldValidateCreateWithProjectId() {

        PostDto postDto = new PostDto();
        postDto.setProjectId(1L);

        postValidator.validateCreate(postDto);

        verify(projectServiceClient).getProject(postDto.getProjectId());
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void shouldValidateUpdate() {

        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("Valid content");

        postValidator.validateUpdate(postDto);


    }

    @Test
    void shouldThrowWhenIdIsNullForUpdate() {

        PostDto postDto = new PostDto();
        postDto.setContent("Valid content");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postValidator.validateUpdate(postDto);
        });

        assertEquals("ID cannot be null when updating a post.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenContentIsNullForUpdate() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postValidator.validateUpdate(postDto);
        });

        assertEquals("Content cannot be null or empty.", exception.getMessage());
    }

    @Test
    void shouldValidatePublish() {
        Post post = new Post();
        post.setPublished(false);

        postValidator.validatePublish(post);
    }

    @Test
    void shouldThrowWhenPostAlreadyPublished() {
        Post post = new Post();
        post.setPublished(true);

        PostException exception = assertThrows(PostException.class, () -> {
            postValidator.validatePublish(post);
        });

        assertEquals("Post is already published", exception.getMessage());
    }

    @Test
    void shouldValidateDelete() {
        Post post = new Post();
        post.setDeleted(false);

        postValidator.validateDelete(post);

    }

    @Test
    void shouldThrowWhenPostAlreadyDeleted() {
        Post post = new Post();
        post.setDeleted(true);

        PostException exception = assertThrows(PostException.class, () -> {
            postValidator.validateDelete(post);
        });

        assertEquals("Post already deleted", exception.getMessage());
    }

}
