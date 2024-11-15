package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    public ResponsePostDto create(CreatePostDto createPostDto) {
        postValidator.validateContent(createPostDto.getContent());
        postValidator.validateAuthorIdAndProjectId(createPostDto.getAuthorId(), createPostDto.getProjectId());
        postValidator.validateAuthorId(createPostDto.getAuthorId());
        postValidator.validateProjectId(createPostDto.getProjectId());

        if (createPostDto.getAuthorId() != null && createPostDto.getProjectId() != null) {
            createPostDto.setProjectId(null);
        }

        Post entity = postMapper.toEntity(createPostDto);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setPublished(false);
        entity.setDeleted(false);

        postRepository.save(entity);

        return postMapper.toDto(entity);
    }

    public ResponsePostDto publish(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnPublished(postId);

        Post post = postRepository.findById(postId).get();

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    public ResponsePostDto update(Long postId, UpdatePostDto updatePostDto) {
        postValidator.validateExistingPostId(postId);
        postValidator.validateContent(updatePostDto.getContent());

        Post post = postRepository.findById(postId).get();

        post.setContent(updatePostDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    public void delete(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnRemoved(postId);

        Post post = postRepository.findById(postId).get();

        post.setDeleted(true);

        postRepository.save(post);
    }

    public ResponsePostDto getById(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnRemoved(postId);

        return postMapper.toDto(postRepository.findById(postId).get());
    }

    public List<ResponsePostDto> getDraftsByUserId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findReadyToPublishByAuthor(userId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> getDraftsByProjectId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findReadyToPublishByProject(userId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> getPublishedByUserId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findPublishedByAuthor(userId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> getPublishedByProjectId(Long projectId) {
        postValidator.validateProjectId(projectId);

        return postRepository.findPublishedByProject(projectId).stream().map(postMapper::toDto).toList();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id: %s not found", id)));
    }
}
