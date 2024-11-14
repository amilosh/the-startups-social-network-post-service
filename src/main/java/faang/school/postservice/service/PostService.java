package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void createDraftPost(PostDto postDto) {
        checkAuthorIdExist(postDto.idUser(), postDto.idProject());
        postRepository.save(postMapper.toEntity(postDto));
    }

    public void publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        post.setPublished(true);
        postRepository.save(post);
    }

    public void updatePost(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        post.setContent(content);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostDto getPost(Long postId) {
        return postRepository.findById(postId)
                .map(postMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    public List<PostDto> getDraftPostsForUser(Long idUser) {
        return postRepository.findByAuthorId(idUser)
                .stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getDraftPostsForProject(Long idProject) {
        return postRepository.findByProjectId(idProject)
                .stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getPublishedPostsForUser(Long idUser) {
        return postRepository.findByAuthorId(idUser)
                .stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getPublishedPostForProject(Long idProject) {
        return postRepository.findByProjectId(idProject)
                .stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkIdUserAndIdProjectNotEquals(Long idUser, Long idProject) {
        if (Objects.equals(idUser, idProject)) {
            log.error(String.format("idProject %s and idUser %s equals", idProject, idUser));
            throw new IllegalArgumentException(String.format("idProject %s and idUser %s equals", idProject, idUser));
        }
    }


    private void checkAuthorIdExist(Long idUser, Long idProject) {
        checkIdUserAndIdProjectNotEquals(idUser, idProject);
        String temp = "User id: ";
        try {
            if (idUser != null) {
                userServiceClient.getUser(idUser);
            } else if (idProject != null) {
                projectServiceClient.getProject(idProject);
                temp = "Project id: ";
            }
        } catch (FeignException e) {
            switch (e.status()) {
                case 404:
                    log.error(temp + idUser + " not found" + HttpStatus.NOT_FOUND);
                    throw new IllegalArgumentException(temp + idUser + " not found" + HttpStatus.NOT_FOUND);
                case 500:
                    log.error(temp + idUser + " Internal Server Error" + HttpStatus.INTERNAL_SERVER_ERROR);
                    throw new IllegalArgumentException(temp + idUser + " Internal Server Error" + HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    log.error(temp + idUser + "Error" + e.getMessage());
                    throw new IllegalArgumentException(temp + idUser + "Error" + e.getMessage());
            }
        }
    }
}
