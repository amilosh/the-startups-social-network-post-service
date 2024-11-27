package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public PostResponseDto createPost(PostRequestDto postDto) {
        isPostAuthorExist(postDto);

        Post post = postMapper.toEntity(postDto);
        post.setId(null);
        post.setPublished(false);
        post.setDeleted(false);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setResources(new ArrayList<>());
        postRepository.save(post);
        log.info("Post with id {} created", post.getId());

        if(postDto.getResources() != null) {
            addResourcesToPost(post.getId(), postDto.getResources());
        }

        return postMapper.toDto(post);
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

    public PostResponseDto updatePost(PostRequestDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(postDto.getContent());

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

    public PostResponseDto getPost(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
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

    private void addResourcesToPost(Long postId, List<ResourceDto> resources) {
        Post post = postRepository.getPostById(postId);
        List<Resource> resourcesToAdd = new ArrayList<>();
        for (ResourceDto dto : resources) {
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                Resource resource = resourceService.add(postId, dto.getFile(), dto.getType());
                log.info("Processing resource of type: {}", dto.getType());
                resourcesToAdd.add(resource);
            }
        }
        post.getResources().addAll(resourcesToAdd);
        log.info("{} resources added to the post with id {}", resourcesToAdd.size(), postId);
        postRepository.save(post);
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
