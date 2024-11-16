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
    public void validateUserExist(Long id){
        userServiceClient.getUser(id);
    }
    public void validateProjectExist(Long id){
        projectServiceClient.getProject(id);
    }

    public void validateCreate(PostDto postDto){
        if (postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postDto.getProjectId());
        }
    }

    public void validatePublish(Post post){
        if (post.isPublished()) {
            try {
                throw new PostException("Post is already published");
            } catch (PostException e) {
                throw new RuntimeException();
            }
        }
    }

    public void validateDelete(Post post){
        if (post.isDeleted()) {
            try {
                throw new PostException("Post already deleted");
            } catch (PostException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
