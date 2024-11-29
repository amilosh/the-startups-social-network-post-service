package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.FeignClientException;
import faang.school.postservice.exception.MessageError;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.exception.UnauthorizedException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisMessagePublisher;
import faang.school.postservice.validator.post.PostValidator;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final RedisMessagePublisher redisMessagePublisher;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Value("${count.default}")
    private Integer defaultValue;

    @Value("${count.add}")
    private Integer addValue;

    @Value("${count.ban}")
    private Integer banCount;

    public PostDto createPost(PostRequestDto postRequestDtoDto) {
        postValidator.checkCreator(postRequestDtoDto);

        Post createPost = postMapper.toEntity(postRequestDtoDto);
        createPost.setPublished(false);
        createPost.setDeleted(false);

        log.info("Post with id {} - created", createPost.getId());
        return postMapper.toDto(postRepository.save(createPost));
    }

    public PostDto publishPost(Long postId) {
        Post publishPost = getPost(postId);
        if (publishPost.isPublished()) {
            throw new PostException("Forbidden republish post");
        }
        publishPost.setPublished(true);

        log.info("Post with id {} - published", publishPost.getId());
        return postMapper.toDto(postRepository.save(publishPost));
    }

    public PostDto updatePost(PostDto postDto) {
        Post post = getPost(postDto.getId());
        postValidator.checkUpdatePost(post, postDto);

        postMapper.updatePostFromDto(postDto, post);

        log.info("Post with id {} - updated", post.getId());
        return postMapper.toDto(postRepository.save(post));
    }

    public void disablePostById(Long postId) {
        Post deletePost = getPost(postId);
        deletePost.setDeleted(true);

        log.info("Post with id {} - deleted", deletePost.getId());
        postRepository.save(deletePost);
    }

    public PostDto getPostById(Long postId) {
        Post post = getPost(postId);

        return postMapper.toDto(post);
    }

    public List<PostDto> getAllNoPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream().filter(post -> !post.isPublished() && !post.isDeleted()).sorted(Comparator.comparing(Post::getCreatedAt).reversed()).toList();

        log.info("Get all drafts posts with author id {}", userId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllNoPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream().filter(post -> !post.isPublished() && !post.isDeleted()).sorted(Comparator.comparing(Post::getCreatedAt).reversed()).toList();

        log.info("Get all drafts posts with project id {}", projectId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(userId).stream().filter(post -> post.isPublished() && !post.isDeleted()).sorted(Comparator.comparing(Post::getPublishedAt).reversed()).toList();

        log.info("Get all posts with author id {}", userId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId).stream().filter(post -> post.isPublished() && !post.isDeleted()).sorted(Comparator.comparing(Post::getPublishedAt).reversed()).toList();

        log.info("Get all posts with project id {}", projectId);
        return postMapper.toDto(posts);
    }

    public void addLikeToPost(long postId, Like like) {
        Post post = getPost(postId);
        post.getLikes().add(like);

        log.info("Adding like to post with id {}", postId);
        postRepository.save(post);
    }

    public void removeLikeFromPost(long postId, Like like) {
        Post post = getPost(postId);
        post.getLikes().remove(like);

        log.info("Removing like from post with id {}", postId);
        postRepository.save(post);
    }

    @Transactional
    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    public void getPostsWhereVerifiedFalse() {
        List<Post> unverifiedPosts = getUnverifiedPosts();
        Map<Long, Integer> authorPostCounts = countPostByAuthor(unverifiedPosts);
        processAuthors(authorPostCounts);
    }

    private List<Post> getUnverifiedPosts() {
        return postRepository.findAllByVerifiedFalse();
    }

    private Map<Long, Integer> countPostByAuthor(List<Post> posts) {
        Map<Long, Integer> authorCounts = new HashMap<>();
        for (Post post : posts) {
            authorCounts.put(post.getAuthorId(), authorCounts.getOrDefault(post.getAuthorId(), defaultValue) + addValue);
        }
        return authorCounts;
    }

    private void processAuthors(Map<Long, Integer> authorPostCounts) {
        for (Map.Entry<Long, Integer> entry : authorPostCounts.entrySet()) {
            Long userId = entry.getKey();
            Integer postCount = entry.getValue();

            log.info("Processing user with id: {} and post count: {}", userId, postCount);

            if (postCount >= banCount) {
                handleUserBan(userId);
            }
        }
    }

    private void handleUserBan(Long userId) {
        try {
            userContext.setUserId(defaultValue);
            UserDto userDto;
            try {
                userDto = userServiceClient.getUser(userId);
                log.info("User id is : "  + userDto.getId() + " banned : " + userDto.isBanned());
            } catch (FeignException.NotFound e) {
                throw new UnauthorizedException(userId, e);
            } catch (FeignException e) {
                String errorMessage = "There was an attempt to get %s by ID: %d".formatted("User", userId);
                throw new FeignClientException(MessageError.FEIGN_CLIENT_UNEXPECTED_EXCEPTION.getMessage(errorMessage), e);
            }
            if (userDto.isBanned()) {
                log.info("User with id: {} is already banned. Skipping...", userId);
                return;
            }
            redisMessagePublisher.sendMessage(userId.toString());
            log.info("Message sent to Redis for user with id: {}", userId);
        } finally {
            userContext.clear();
        }
    }

}