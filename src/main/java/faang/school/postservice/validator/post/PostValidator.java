package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.excaption.post.PostException;
import faang.school.postservice.model.Post;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateUserExist(Long id) {
        userServiceClient.getUser(id);
    }

    public void validateProjectExist(Long id) {
        projectServiceClient.getProject(id);
    }

    public void validateCreate(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postDto.getProjectId());
        }
        if (postDto.getId() != null) {
            throw new IllegalArgumentException("ID must be null when creating a new post.");
        }
    }

    public void validateUpdate(PostDto postDto) {
        if (postDto.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null when updating a post.");
        }
        if (postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty.");
        }
    }

    public void validatePublish(Post post) {
        if (post.isPublished()) {
            throw new PostException("Post is already published");
        }
    }

    public void validateDelete(Post post) {
        if (post.isDeleted()) {

            throw new PostException("Post already deleted");
        }
    }


}
