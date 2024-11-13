package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityWasRemovedException;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;

    public void validateContent(String content) {
        if (content.isBlank()) {
            log.error("Post content cannot be blank");
            throw new DataValidationException("Post content cannot be blank");
        }
    }

    public void validateAuthorIdAndProjectId(Long authorId, Long projectId) {
        if (authorId == null && projectId == null) {
            log.error("Author id and project id cannot be null at the same time");
            throw new DataValidationException("Author id and project id cannot be null at the same time");
        }
    }

    public void validateExistingPostId(Long postId) {
        if (postRepository.findById(postId).isEmpty()) {
            log.error("Post with id {} not found", postId);
            throw new DataValidationException("Post with id " + postId + " not found");
        }
    }

    public void validateAuthorId(Long authorId) {
        if (authorId != null && userServiceClient.getUser(authorId).getId() == null) {
            log.error("User with id {} not found", authorId);
            throw new DataValidationException("User with id " + authorId + " not found");
        }
    }

    public void validateProjectId(Long projectId) {
        if (projectId != null && projectServiceClient.getProject(projectId).getId() == 0) {
            log.error("Project with id {} not found", projectId);
            throw new DataValidationException("Project with id " + projectId + " not found");
        }
    }

    public void validatePostIdOnRemoved(Long postId) {
        if (postRepository.findById(postId).get().isDeleted()) {
            log.error("Post with id {} was removed", postId);
            throw new EntityWasRemovedException("Post with id " + postId + " was removed");
        }
    }

    public void validatePostIdOnPublished(Long postId) {
        if (postRepository.findById(postId).get().isPublished()) {
            log.error("Post with id {} was published", postId);
            throw new DataValidationException("Post with id " + postId + " was published");
        }
    }
}
