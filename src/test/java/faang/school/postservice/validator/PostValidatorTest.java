package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityWasRemovedException;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.HashtagService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private HashtagService hashtagService;

    @InjectMocks
    private PostValidator postValidator;

    Long postId = 1L;

    @Test
    void validateContentWhenContentIsBlankShouldThrowDataValidationException() {
        String content = "";
        assertThrows(DataValidationException.class, () -> postValidator.validateContent(content));
    }

    @Test
    void validateContentWhenContentIsNotBlankShouldNotThrowException() {
        String content = "Valid content";
        postValidator.validateContent(content);
    }

    @Test
    void validateAuthorIdAndProjectIdWhenBothIdsAreNullShouldThrowDataValidationException() {
        Long authorId = null;
        Long projectId = null;

        assertThrows(DataValidationException.class, () -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    void validateAuthorIdAndProjectIdWhenAuthorIdIsNotNullShouldNotThrowException() {
        Long authorId = 1L;
        Long projectId = null;

        postValidator.validateAuthorIdAndProjectId(authorId, projectId);
    }

    @Test
    void validateAuthorIdAndProjectIdWhenProjectIdIsNotNullShouldNotThrowException() {
        Long authorId = null;
        Long projectId = 1L;

        postValidator.validateAuthorIdAndProjectId(authorId, projectId);
    }

    @Test
    void validateExistingPostIdWhenPostIdNotFoundShouldThrowDataValidationException() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postValidator.validateExistingPostId(postId));
    }

    @Test
    void validateExistingPostIdWhenPostIdExistsShouldNotThrowException() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));

        postValidator.validateExistingPostId(postId);
    }

    @Test
    void validateAuthorIdWhenAuthorIdIsNotNullAndUserExistsShouldNotThrowException() {
        Long authorId = 1L;
        UserDto user = new UserDto(
                authorId,
                "username",
                "email"
        );

        when(userServiceClient.getUser(authorId)).thenReturn(user);
        postValidator.validateAuthorId(authorId);
    }

    @Test
    void validateProjectIdWhenProjectIdIsNotNullAndProjectExistsShouldNotThrowException() {
        Long projectId = 1L;
        Long authorId = 1L;

        ProjectDto project = new ProjectDto();
        project.setId(projectId);

        when(projectServiceClient.getProjectById(projectId, authorId)).thenReturn(project);

        postValidator.validateProjectId(projectId, authorId);
    }

    @Test
    void validatePostIdOnRemovedWhenPostIsDeletedShouldThrowEntityWasRemovedException() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(true);

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        assertThrows(EntityWasRemovedException.class, () -> postValidator.validatePostIdOnRemoved(postId));
    }

    @Test
    void validatePostIdOnRemovedWhenPostIsNotDeletedShouldNotThrowException() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        postValidator.validatePostIdOnRemoved(postId);
    }

    @Test
    void validatePostIdOnPublishedWhenPostIsPublishedShouldThrowDataValidationException() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(true);

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        assertThrows(DataValidationException.class, () -> postValidator.validatePostIdOnPublished(postId));
    }

    @Test
    void validatePostIdOnPublishedWhenPostIsNotPublishedShouldNotThrowException() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);

        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        postValidator.validatePostIdOnPublished(postId);
    }

    @Test
    @DisplayName("Check post exists by id success")
    void testValidatePostExistsById() {
        when(postRepository.existsById(postId)).thenReturn(true);

        postValidator.validatePostExistsById(postId);

        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    @DisplayName("Check post exists by id fail")
    void testValidatePostExistsByIdFail() {
        when(postRepository.existsById(postId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> postValidator.validatePostExistsById(postId));
        assertEquals("Post with id: 1 doesn't exist", ex.getMessage());
    }
}