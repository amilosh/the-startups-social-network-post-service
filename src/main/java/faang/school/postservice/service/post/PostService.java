package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.*;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.amazons3.Amazons3ServiceImpl;
import faang.school.postservice.service.amazons3.processing.KeyKeeper;
import faang.school.postservice.service.post.corrector.rapid.GingerCorrector;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import faang.school.postservice.validator.dto.project.ProjectDtoValidator;
import faang.school.postservice.validator.dto.user.UserDtoValidator;
import faang.school.postservice.validator.file.FileValidator;
import faang.school.postservice.validator.post.PostIdValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;
    private final AlbumService albumService;
    private final ResourceServiceImpl resourceServiceImpl;
    private final UserDtoValidator userDtoValidator;
    private final ProjectDtoValidator projectDtoValidator;
    private final PostIdValidator postIdValidator;
    private final Amazons3ServiceImpl amazonS3;
    private final FileValidator fileValidator;
    private final KeyKeeper keyKeeper;
    private final GingerCorrector gingerCorrector;

    @Transactional
    public PostDraftResponseDto createDraftPost(@NotNull @Valid PostDraftCreateDto dto) {
        validateUserOrProject(dto.getAuthorId(), dto.getProjectId());

        Post postEntity = postMapper.toEntityFromDraftDto(dto);
        if (dto.getAlbumsId() != null) {
            postEntity.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
        }
        if (dto.getResourcesId() != null) {
            postEntity.setResources(resourceServiceImpl.getResourcesByIds(dto.getResourcesId()));
        }
        return postMapper.toDraftDtoFromPost(postRepository.save(postEntity));
    }

    @Transactional
    public PostDraftResponseDto createDraftPostWithFiles(
            PostDraftWithFilesCreateDto dto, MultipartFile[] files) throws IOException {
        validateUserOrProject(dto.getAuthorId(), dto.getProjectId());
        fileValidator.checkFiles(files);
        Post post = postMapper.toEntityFromDraftDtoWithFiles(dto);
        if (dto.getAlbumsId() != null) {
            post.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
        }

        List<Resource> resources = new ArrayList<>();
        uploadAndAddFiles(resources, files, post);
        post.setResources(resources);
        return postMapper.toDraftDtoFromPost(postRepository.save(post));
    }

    public PostResponseDto publishPost(@Positive long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    public PostResponseDto updatePost(@Positive long postId, @NotNull @Valid PostUpdateDto dto) {
        Post post = getPostById(postId);
        post.setContent(dto.getContent());
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    public PostResponseDto updatePostWithFiles(
            @Positive long postId, @NotNull @Valid PostUpdateDto dto, MultipartFile[] files) throws IOException {
        fileValidator.checkFiles(files);
        Post post = getPostById(postId);
        fileValidator.checkingTotalOfFiles(files.length, post.getResources().size());

        List<Resource> resourcesFromPostInDateBase = post.getResources();
        List<Resource> newResourcesToUpdate = resourceServiceImpl.getResourcesByIds(dto.getResourcesIds());

        removeIrrelevantResourcesFromMinio(resourcesFromPostInDateBase, newResourcesToUpdate);
        uploadAndAddFiles(newResourcesToUpdate, files, post);

        post.setResources(newResourcesToUpdate);
        post.setContent(dto.getContent());
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    public PostResponseDto deletePost(@Positive long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    public Post findPostById(Long postId) {
        postIdValidator.postIdValidate(postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    public PostResponseDto getPost(@Positive long postId) {
        return postMapper.toDtoFromPost(getPostById(postId));
    }

    public boolean existsPost(Long postId) {
        postIdValidator.postIdValidate(postId);
        return postRepository.existsById(postId);
    }

    public List<PostDraftResponseDto> getDraftPostsByUserIdSortedCreatedAtDesc(long userId) {
        return postRepository.findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
                .map(postMapper::toDraftDtoFromPost)
                .toList();
    }

    public List<PostDraftResponseDto> getDraftPostsByProjectIdSortedCreatedAtDesc(long projectId) {
        return postRepository.findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId).stream()
                .map(postMapper::toDraftDtoFromPost)
                .toList();
    }

    public List<PostResponseDto> getPublishPostsByUserIdSortedCreatedAtDesc(long userId) {
        return postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
                .map(postMapper::toDtoFromPost)
                .toList();
    }

    public List<PostResponseDto> getPublishPostsByProjectIdSortedCreatedAtDesc(long projectId) {
        return postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId).stream()
                .map(postMapper::toDtoFromPost)
                .toList();
    }

    @Async("workerPool")
    public void checkingPostForErrors() throws IOException, InterruptedException {
        List<Post> posts = postRepository.findByNotPublished();

        int batchSize = 5;
        int totalPosts = posts.size();
        for (int i = 0; i < totalPosts; i += batchSize) {
            int end = Math.min(i + batchSize, totalPosts);
            List<Post> batch = posts.subList(i, end);

            checkingGroupPost(batch);
        }
    }

    @Async("workerPool")
    protected void checkingGroupPost(List<Post> posts) throws IOException, InterruptedException {
        List<Post> checkPosts = gingerCorrector.correct(posts);
        postRepository.saveAll(checkPosts);
    }

    private Post getPostById(@Positive long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    private void validateUserOrProject(Long userId, Long projectId) {
        if (userId != null) {
            userDtoValidator.validateUserDto(userService.getUser(userId));
        }
        if (projectId != null) {
            projectDtoValidator.validateProjectDto(projectService.getProject(projectId));
        }
    }

    private void uploadAndAddFiles(
            List<Resource> resources, MultipartFile[] files, Post post) throws IOException {
        String folder = String.format("%d:%s:%d", post.getAuthorId(), "files", post.getProjectId());
        for (MultipartFile file : files) {
            String key = keyKeeper.getKeyFile(folder);
            amazonS3.uploadFile(file, key);
            Resource resource = createdResource(file, key);
            resource.setPost(post);
            resources.add(resource);
        }
    }

    private void removeIrrelevantResourcesFromMinio(List<Resource> resourcesFromDateBase, List<Resource> resourcesToUpdate) {
        for (Resource resource : resourcesFromDateBase) {
            if (!resourcesToUpdate.contains(resource)) {
                amazonS3.deleteFile(resource.getKey());
            }
        }
    }

    private Resource createdResource(MultipartFile file, String key) {
        return resourceServiceImpl.save(
                Resource.builder()
                        .name(file.getOriginalFilename())
                        .key(key)
                        .size(file.getSize())
                        .type(file.getContentType())
                        .build());
    }
}