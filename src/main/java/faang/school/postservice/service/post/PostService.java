package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.filter.PostFilter;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final PostFilter postFilter;


    public PostDto create(PostDto postDto) {
        postValidator.validateCreate(postDto);

        postDto.setCreatedAt(LocalDateTime.now());

        Post post = postMapper.toEntity(postDto);

        post.setPublished(false);
        post.setDeleted(false);
        postRepository.save(post);
        return postDto;

    }

    public PostDto publishPost(Long id) {
        Post post = postRepository
                .findById(id)
                .orElseThrow(EntityExistsException::new);
        postValidator.validatePublish(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.
                findById(postDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto deletePost(Long id) {
        Post post = postRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postValidator.validateDelete(post);

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);

        PostDto postDto = postMapper.toDto(post);
        postDto.setDeleted(true);
        return postDto;
    }

    public PostDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<PostDto> getAllNonPublishedByAuthorId(Long id) {
        postValidator.validateUserExist(id);
        return postFilter.filterPostByTimeToDTo(postRepository.findByAuthorId(id), false);
    }

    public List<PostDto> getAllNonPublishPostByProjectId(Long id) {
        postValidator.validateProjectExist(id);
        return postFilter.filterPostByTimeToDTo(postRepository.findByAuthorId(id), false);
    }

    public List<PostDto> getAllPublishedByAuthorId(Long id) {
        postValidator.validateUserExist(id);
        return postFilter.filterPostByTimeToDTo(postRepository.findByAuthorId(id), true);
    }

    public List<PostDto> getAllPublishPostByProjectId(Long id) {
        postValidator.validateProjectExist(id);
        return postFilter.filterPostByTimeToDTo(postRepository.findByAuthorId(id), true);
    }

}
