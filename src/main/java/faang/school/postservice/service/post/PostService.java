package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final ResourceService resourceService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ResourceValidator resourceValidator;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        isPostAuthorExist(requestDto);
        Post post = postMapper.toEntity(requestDto);
        post.setPublished(false);
        post.setDeleted(false);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setResources(new ArrayList<>());

        post = postRepository.save(post);

        uploadResourcesToPost(requestDto.getImages(), "image", post);
        uploadResourcesToPost(requestDto.getAudio(), "audio", post);

        log.info("Post with id {} created", post.getId());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto updatePost(Long postId, PostUpdateDto updateDto) {
        Post post = postRepository.getPostById(postId);

        if (updateDto.getContent() != null) {
            post.setContent(updateDto.getContent());
        }

        deleteResourcesFromPost(updateDto.getImageFilesIdsToDelete());
        deleteResourcesFromPost(updateDto.getAudioFilesIdsToDelete());

        uploadResourcesToPost(updateDto.getImages(), "image", post);
        uploadResourcesToPost(updateDto.getAudio(), "audio", post);

        resourceValidator.validateResourceCounts(post);

        post.setUpdatedAt(LocalDateTime.now());
        log.info("Post with id {} updated", postId);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.getPostById(postId);
        return postMapper.toDto(post);
    }

    private void uploadResourcesToPost(List<MultipartFile> files, String resourceType, Post post) {
        if (files != null) {
            log.info("Uploading {} {} resources for post ID {}", files.size(), resourceType, post.getId());
            List<Resource> resources = resourceService.uploadResources(files, resourceType, post);
            post.getResources().addAll(resources);
            log.info("{} {} resources uploaded successfully for post ID {}", resources.size(), resourceType, post.getId());
        }
    }

    private void deleteResourcesFromPost(List<Long> resourceIds) {
        if (resourceIds != null) {
            log.info("Deleting {} resources", resourceIds.size());
            resourceService.deleteResources(resourceIds);
            log.info("{} resources deleted successfully", resourceIds.size());
        }
    }

    public PostResponseDto publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isPublished()) {
            throw new DataValidationException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostResponseDto deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isDeleted()) {
            throw new DataValidationException("post already deleted");
        }

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);

        PostResponseDto postDto = postMapper.toDto(post);
        postDto.setDeletedAt(LocalDateTime.now());

        return postDto;
    }


    public List<PostResponseDto> getAllNonPublishedByAuthorId(Long id) {
        validateUserExist(id);

        return filterNonPublishedPostsByTimeToDto(postRepository.findByAuthorIdWithLikes(id));
    }

    public List<PostResponseDto> getAllNonPublishedByProjectId(Long id) {
        validateProjectExist(id);

        return filterNonPublishedPostsByTimeToDto(postRepository.findByProjectIdWithLikes(id));
    }

    public List<PostResponseDto> getAllPublishedByAuthorId(Long id) {
        validateUserExist(id);

        return filterPublishedPostsByTimeToDto(postRepository.findByAuthorIdWithLikes(id));
    }

    public List<PostResponseDto> getAllPublishedByProjectId(Long id) {
        validateProjectExist(id);

        return filterPublishedPostsByTimeToDto(postRepository.findByProjectIdWithLikes(id));
    }

    private void validateUserExist(Long id) {
        userServiceClient.getUser(id);
    }

    private void validateProjectExist(Long id) {
        projectServiceClient.getProject(id);
    }

    private List<PostResponseDto> filterPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostResponseDto> filterNonPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private void isPostAuthorExist(PostRequestDto postDto) {
        if (postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postDto.getProjectId());
        }
    }

}
