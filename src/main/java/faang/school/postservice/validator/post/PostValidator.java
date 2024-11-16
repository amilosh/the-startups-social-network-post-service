package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void checkCreator(PostRequestDto postRequestDto) {
        if (postRequestDto.getAuthorId() != null && postRequestDto.getProjectId() != null) {
            throw new PostException("Forbidden have author and project");
        }
        if (postRequestDto.getAuthorId() == null && postRequestDto.getProjectId() == null) {
            throw new PostException("Necessary indicate the author or project");
        }
        if (postRequestDto.getAuthorId() != null) {
            userServiceClient.getUser(postRequestDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postRequestDto.getProjectId());
        }
    }

    public void checkUpdatePost(Post post, PostDto postDto) {
        if (post.getAuthorId() != postDto.getAuthorId() || post.getProjectId() != postDto.getProjectId()) {
            throw new PostException("Forbidden change author of post");
        }
    }
}
