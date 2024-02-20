package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PostValidator postValidator;
    private final long authorId = 3L;
    private final long projectId = 4L;
    private List<ProjectDto> projectDtos;
    private final PostDto validPostDto = PostDto.builder().content("content").build();

    public PostValidatorTest() {
    }

    @BeforeEach
    void init() {
        ProjectDto projectDto1 = ProjectDto.builder().id(projectId + 1).build();
        ProjectDto projectDto2 = ProjectDto.builder().id(projectId + 2).build();
        ProjectDto projectDto3 = ProjectDto.builder().id(projectId + 3).build();
        projectDtos = new ArrayList<>(List.of(projectDto1, projectDto2, projectDto3));
    }

    @Test
    void testValidateAccessToPost_bothIdsNull_throwsDataValidationException() {
        assertThrows(
                DataValidationException.class,
                () -> postValidator.validateAccessToPost(null, null)
        );
    }

    @Test
    void testValidateAccessToPost_idsEquals_throwsDataValidationException() {
        long anyId = 3L;
        assertThrows(
                DataValidationException.class,
                () -> postValidator.validateAccessToPost(anyId, anyId)
        );
    }

    @Test
    void testValidateAccessToPost_authorNotExist_throwsEntityNotFoundException() {
        mockExistsUserById(false);
        assertThrows(
                EntityNotFoundException.class,
                () -> postValidator.validateAccessToPost(authorId, null)
        );
    }

    @Test
    void testValidateAccessToPost_authorIdNotFromContext_throwsSecurityException() {
        mockExistsUserById(true);
        mockUserContext(false);
        assertThrows(
                SecurityException.class,
                () -> postValidator.validateAccessToPost(authorId, null)
        );
    }

    @Test
    void testValidateAccessToPost_authorExistsAndFromContext_nothingHappens() {
        mockExistsUserById(true);
        mockUserContext(true);
        postValidator.validateAccessToPost(authorId, null);
    }

    @Test
    void testValidateAccessToPost_projectNotExist_throwsEntityNotFoundException() {
        mockExistProjectById(false);
        assertThrows(
                EntityNotFoundException.class,
                () -> postValidator.validateAccessToPost(null, projectId)
        );
    }

    @Test
    void testValidateAccessToPost_projectNotOwner_throwsSecurityException() {
        mockExistProjectById(true);
        mockGetAllProject(false);

        assertThrows(
                SecurityException.class,
                () -> postValidator.validateAccessToPost(null, projectId)
        );
    }

    @Test
    void testValidateAccessToPost_projectExistsAndProjectIsOwner_nothingHappens() {
        mockExistProjectById(true);
        mockGetAllProject(true);

        postValidator.validateAccessToPost(null, projectId);
    }

    @Test
    void testValidatePostContent_validContent_nothingHappens() {
        postValidator.validatePostContent(validPostDto.getContent());
    }

    @Test
    void testValidatePostContent_notValidContent_throwsDataValidationException() {
        String invalidPostContent1 = "";
        String invalidPostContent2 = "    ";
        String invalidPostContent3 = null;

        assertThrowsPostContent(invalidPostContent1);
        assertThrowsPostContent(invalidPostContent2);
        assertThrowsPostContent(invalidPostContent3);
    }

    private void assertThrowsPostContent(String invalidPostContent) {
        assertThrows(
                DataValidationException.class,
                () -> postValidator.validatePostContent(invalidPostContent)
        );
    }

    private void mockExistsUserById(boolean isExist) {
        when(userServiceClient.existsUserById(authorId)).thenReturn(isExist);
    }

    private void mockExistProjectById(boolean isExist) {
        when(projectServiceClient.existProjectById(projectId)).thenReturn(isExist);
    }

    private void mockUserContext(boolean fromContext) {
        if (fromContext) {
            when(userContext.getUserId()).thenReturn(authorId);
        }
        if (!fromContext) {
            when(userContext.getUserId()).thenReturn(authorId + 1);
        }
    }

    private void mockGetAllProject(boolean userHasAccess) {
        if (userHasAccess) {
            ProjectDto projectDto = ProjectDto.builder().id(projectId).build();
            projectDtos.add(projectDto);
            when(projectServiceClient.getAll()).thenReturn(projectDtos);
        }
        if (!userHasAccess) {
            when(projectServiceClient.getAll()).thenReturn(projectDtos);
        }
    }
}
