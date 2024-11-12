package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void checkCreator(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new PostException("Forbidden have author and project");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new PostException("Necessary indicate the author or project");
        }
        if (postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postDto.getProjectId());
        }
    }

    public void checkUpdatePost(Post post, PostDto postDto) {
        if (!post.getAuthorId().equals(postDto.getAuthorId()) || !post.getProjectId().equals(postDto.getProjectId())) {
            throw new PostException("Forbidden change author of post");
        }
    }
}
