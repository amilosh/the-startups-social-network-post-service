package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ReturnPostDto;

import java.util.List;

public interface PostService {
    ReturnPostDto createPost(PostDto postDto);

    ReturnPostDto publishPost(Long id);

    ReturnPostDto updatePost(PostDto postDto);

    ReturnPostDto deletePost(Long id);

    ReturnPostDto getPost(Long id);

    List<ReturnPostDto> getAllNonPublishedByAuthorId(Long id);

    List<ReturnPostDto> getAllNonPublishedByProjectId(Long id);

    List<ReturnPostDto> getAllPublishedByAuthorId(Long id);

    List<ReturnPostDto> getAllPublishedByProjectId(Long id);
}
