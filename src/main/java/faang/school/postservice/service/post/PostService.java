package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.event.kafka.post.PostCreateEvent;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.event.post.PostViewEvent;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.producer.post.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.publisher.post.PostViewEventPublisher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${post.moderation.sublist.length}")
    private Long sublistLength;

    private final PostMapper postMapper;
    private final UserContext userContext;
    private final ExecutorService executor;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final ModerationDictionary moderationDictionary;
    private final PostViewEventPublisher postViewEventPublisher;

    public PostResponseDto getPost(long postId) {
        Post post = findById(postId);

        publishEvent(postId, post.getAuthorId());

        return postMapper.toResponseDto(post, post.getLikes().size());
    }

    public List<Post> getAllPostsNotPublished() {
        return postRepository.findReadyToPublish();
    }

    public void savePosts(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post service. Post not found. id: " + postId));
    }

    public List<PostResponseDto> getPostsByAuthorWithLikes(long authorId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(authorId);
        return posts.stream()
                .map(post -> postMapper.toResponseDto(post, post.getLikes().size()))
                .toList();
    }

    public List<PostResponseDto> getPostsByProjectWithLikes(long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId);
        return posts.stream()
                .map(post -> postMapper.toResponseDto(post, post.getLikes().size()))
                .toList();
    }

    public List<Long> findAuthorsWithUnverifiedPosts(int limit, LocalDate fromDate) {
        return postRepository.findAuthorsWithUnverifiedPosts(limit, fromDate);
    }

    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        log.info("start createPost with {}", postRequestDto);

        UserDto author = userServiceClient.getUser(userContext.getUserId());

        Post post = postRepository.save(buildPost(postRequestDto, author));
        log.info("save post in DB: {}", post);

        sendPostEvent(post, author);

        return postMapper.toDto(post);
    }

    @Async("executor")
    public void moderatePostsContent() {
        List<Post> unverifiedPosts = postRepository.findReadyToVerified();

        for (int i = 0; i < unverifiedPosts.size(); i += sublistLength) {
            List<Post> subList = unverifiedPosts.subList(i, (int) Math.min(unverifiedPosts.size(), i + sublistLength));

            CompletableFuture<Void> verifiedEntities =
                    CompletableFuture.runAsync(() -> moderationDictionary.searchSwearWords(subList), executor);

            verifiedEntities.thenAccept(result -> postRepository.saveAll(subList));
        }
    }

    private Post buildPost(PostRequestDto postRequestDto, UserDto author) {
        return Post.builder()
                .content(postRequestDto.getContent())
                .authorId(author.getId())
                .build();
    }

    private void publishEvent(Long postId, Long postAuthorId) {
        PostViewEvent postViewEvent = PostViewEvent.builder()
                .postId(postId)
                .authorId(postAuthorId)
                .userId(userContext.getUserId())
                .viewTime(LocalDateTime.now())
                .build();
        try {
            postViewEventPublisher.publish(postViewEvent);
        } catch (Exception ex) {
            log.error("Failed to send notification with postViewEvent: {}", postViewEvent.toString(), ex);
        }
    }

    private void sendPostEvent(Post post, UserDto author) {
        PostCreateEvent postCreateEvent = PostCreateEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .subscribers(author.getFollowers())
                .build();
        try {
            kafkaPostProducer.sendEvent(postCreateEvent);
        } catch (Exception ex) {
            log.error("Failed to send postCreateEvent: {}", postCreateEvent.toString(), ex);
        }
    }
}
