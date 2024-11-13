package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.response.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    public PostDto create(CreatePostDto createPostDto) {
        postValidator.validateContent(createPostDto.getContent());
        postValidator.validateAuthorIdAndProjectId(createPostDto.getAuthorId(), createPostDto.getProjectId());
        postValidator.validateAuthorId(createPostDto.getAuthorId());
        postValidator.validateProjectId(createPostDto.getProjectId());

        if (createPostDto.getAuthorId() != null && createPostDto.getProjectId() != null) {
            createPostDto.setProjectId(null);
        }

        Post entity = postMapper.toEntity(createPostDto);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setScheduledAt(LocalDateTime.now());
        entity.setPublished(false);
        entity.setDeleted(false);
        entity.setUpdatedAt(null);
        entity.setId(0);

        postRepository.save(entity);

        return postMapper.toDto(entity);
    }

    public PostDto publish(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnPublished(postId);

        Post post = postRepository.findById(postId).get();

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    public PostDto update(Long postId, UpdatePostDto updatePostDto) {
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

    public PostDto getById(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnRemoved(postId);

        return postMapper.toDto(postRepository.findById(postId).get());
    }

    public List<PostDto> getDraftByUserId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findReadyToPublishByAuthor(userId).stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> getDraftByProjectId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findReadyToPublishByProject(userId).stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> getPublishedByUserId(Long userId) {
        postValidator.validateAuthorId(userId);

        return postRepository.findPublishedByAuthor(userId).stream().map(postMapper::toDto).toList();
    }

    public List<PostDto> getPublishedByProjectId(Long projectId) {
        postValidator.validateProjectId(projectId);

        return postRepository.findPublishedByProject(projectId).stream().map(postMapper::toDto).toList();
    }
}
