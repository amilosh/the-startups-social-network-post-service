package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.resource.ResourceValidator;
import faang.school.postservice.service.post.filter.PostFilters;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    private final PostValidator postValidator;
    private final List<PostFilters> postFilters;

    public PostResponseDto create(PostRequestDto postRequestDto) {
        postValidator.validateCreate(postRequestDto);

        Post post = postMapper.toEntity(postRequestDto);

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
        Post post = postValidator.validateAndGetPostById(id);
        postValidator.validatePublish(post);
        post.setPublished(true);
        post.setDeleted(false);

        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto deletePost(Long id) {
        Post post = postRepository.findById(id)
    public PostResponseDto updatePost(PostUpdateDto postDto) {
        Objects.requireNonNull(postDto, "PostUpdateDto cannot be null");

        Post post = postValidator.validateAndGetPostById(postDto.getId());
        post.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(post));
    }

    public void deletePost(Long id) {
        Post post = postRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postValidator.validateDelete(post);

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostResponseDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<PostResponseDto> getPosts(PostFilterDto filterDto) {
        Stream<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false);

        postFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(posts, filterDto));

        return postMapper.toDtoList(posts.toList());
    }
}
