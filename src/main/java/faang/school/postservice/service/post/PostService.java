package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;

    public List<PostDto> getAllPostByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();

        log.info("Get all posts with author id {}", userId);
        return postMapper.toDto(posts);
    }

    public List<PostDto> getAllPostByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();

        log.info("Get all posts with project id {}", projectId);
        return postMapper.toDto(posts);
    }

    public Post getPostEntity(Long postId) {
        return getPost(postId);
    }

    public void addLikeToPost(long postId, Like like) {
        Post post = getPost(postId);
        post.getLikes().add(like);

        log.info("Adding like to post with id {}", postId);
        postRepository.save(post);
    }

    public void removeLikeFromPost(long postId, Like like) {
        Post post = getPost(postId);
        post.getLikes().add(like);

        log.info("Removing like from post with id {}", postId);
    }

    private Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("Post with id " + postId + " not found"));

        log.info("Get post with id {}", postId);
        return post;
    }
}