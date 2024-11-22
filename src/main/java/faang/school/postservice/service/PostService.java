package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.HashtagValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final HashtagService hashtagService;
    private final HashtagValidator hashtagValidator;

    @Transactional
    public ResponsePostDto create(CreatePostDto createPostDto) {
        postValidator.validateContent(createPostDto.getContent());
        postValidator.validateAuthorIdAndProjectId(createPostDto.getAuthorId(), createPostDto.getProjectId());
        postValidator.validateAuthorId(createPostDto.getAuthorId());
        postValidator.validateProjectId(createPostDto.getProjectId(), createPostDto.getAuthorId());

        validateHashtags(createPostDto.getHashtags());

        if (createPostDto.getAuthorId() != null && createPostDto.getProjectId() != null) {
            createPostDto.setProjectId(null);
        }

        Post entity = postMapper.toEntity(createPostDto);

        entity.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));
        entity.setPublished(false);
        entity.setDeleted(false);

        if (hasHashtags(createPostDto.getHashtags())) {
            entity.setHashtags(getAndCreateHashtags(createPostDto.getHashtags()));
        }

        postRepository.save(entity);

        return postMapper.toDto(entity);
    }

    @Transactional
    public ResponsePostDto publish(Long postId) {
        postValidator.validateExistingPostId(postId);
        postValidator.validatePostIdOnPublished(postId);

        Post post = postRepository.findById(postId).get();

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now(ZoneId.of("UTC+3")));
        post.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto update(Long postId, UpdatePostDto updatePostDto) {
        postValidator.validateExistingPostId(postId);
        postValidator.validateContent(updatePostDto.getContent());

        validateHashtags(updatePostDto.getHashtags());

        Post post = postRepository.findById(postId).get();


        if (hasHashtags(updatePostDto.getHashtags())) {
            post.setHashtags(getAndCreateHashtags(updatePostDto.getHashtags()));
        }

        post.setContent(updatePostDto.getContent());
        post.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC+3")));

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
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

    public List<ResponsePostDto> getPublishedByProjectId(Long projectId, Long authorId) {
        postValidator.validateProjectId(projectId, authorId);

        return postRepository.findPublishedByProject(projectId).stream().map(postMapper::toDto).toList();
    }

    public List<ResponsePostDto> findByHashtags(String tag) {
        hashtagValidator.validateHashtag(tag);

        return postRepository.findByHashtags(tag).stream().map(postMapper::toDto).toList();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with id: %s not found", id)));
    }

    private void validateHashtags(List<String> hashtags) {
        if (hashtags != null) {
            for (String hashtag : hashtags) {
                hashtagValidator.validateHashtag(hashtag);
            }
        }
    }

    private boolean hasHashtags(List<String> hashtags) {
        return hashtags != null && !hashtags.isEmpty();
    }
    
    private Set<Hashtag> getAndCreateHashtags(List<String> hashtags) {
        Set<Hashtag> result = new HashSet<>();
        for (String tag : hashtags) {
            Hashtag hashtag = hashtagService.findByTag(tag)
                    .orElseGet(() -> hashtagService.create(tag));
            result.add(hashtag);
        }
        return result;
    }
}
