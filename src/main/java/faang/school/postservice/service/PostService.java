package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    public PostDto createPostDraft(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new PostValidationException("A post can be created by a user or a project");
        }

        Long authorId = postDto.getAuthorId();
        if (authorId != null) {
            try {
                UserDto author = userServiceClient.getUser(authorId);
                if (author == null) {
                    throw new PostValidationException("User not found with ID: " + authorId);
                }
            } catch (FeignException.NotFound e) {
                log.warn("User not found with ID: {}", authorId, e);
                throw new ExternalServiceException("User Service returned 404 - User not found with ID: " + authorId);
            } catch (FeignException e) {
                log.error("Error while communicating with User Service: {}", e.getMessage(), e);
                throw new ExternalServiceException("Failed to communicate with User Service. Please try again later.");
            }
        }

        Long projectId = postDto.getProjectId();
        if (projectId != null) {
            try {
                ProjectDto project = projectServiceClient.getProject(projectId);
                if (project == null) {
                    throw new PostValidationException("Project not found with ID: " + projectId);
                }
            } catch (FeignException.NotFound e) {
                log.warn("Project not found with ID: {}", projectId, e);
                throw new ExternalServiceException("Project Service returned 404 - Project not found with ID: " + projectId);
            } catch (FeignException e) {
                log.error("Error while communicating with Project Service: {}", e.getMessage(), e);
                throw new ExternalServiceException("Failed to communicate with Project Service. Please try again later.");
            }
        }

        Post postToSave = postMapper.toEntity(postDto);
        postToSave.setPublished(false);
        postToSave.setDeleted(false);
        Post savedPost = postRepository.save(postToSave);
        log.info("Post was created with ID: {}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        if (post.isPublished()) {
            throw new PostValidationException("Post with ID: " + postId + " is already published");
        }
        if (post.isDeleted()) {
            throw new PostValidationException("Post with ID: " + postId + " was deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(post);
        log.info("Post with ID: {} was published", postId);
        return postMapper.toDto(publishedPost);
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        if (postDto.getContent() != null) {
            if (postDto.getContent().isBlank()) {
                throw new PostValidationException("Post content cannot be empty, post ID: " + postId);
            }
            post.setContent(postDto.getContent());
            post.setUpdatedAt(LocalDateTime.now());
        }

        Post updatedPost = postRepository.save(post);
        log.info("Post with ID: {} was updated", postId);
        return postMapper.toDto(updatedPost);
    }

    public void softDelete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        post.setDeleted(true);
        postRepository.save(post);
        log.info("Post with ID: {} was deleted softly", postId);
    }

    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        log.info("Fetch the post with ID: {}", postId);
        return postMapper.toDto(post);
    }

    public List<PostDto> getAllPostDraftsByUserId(Long userId) {
        List<Post> postDrafts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .toList();
        log.info("Fetch all post drafts of the user with ID: {}", userId);
        return postMapper.toDto(postDrafts);
    }

    public List<PostDto> getAllPostDraftsByProjectId(Long projectId) {
        List<Post> postDrafts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .toList();
        log.info("Fetch all post drafts of the project with ID: {}", projectId);
        return postMapper.toDto(postDrafts);
    }

    public List<PostDto> getAllPublishedPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> post.isPublished() && post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt))
                .toList();
        log.info("Fetch all posts of the user with ID: {}", userId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt))
                .toList();
        log.info("Fetch all posts of the project with ID: {}", projectId);
        return postMapper.toDto(posts);
    }
}
