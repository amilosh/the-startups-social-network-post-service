package faang.school.postservice.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public Long createDraftPost(PostDto postDto) {
        checkAuthorIdExist(postDto.userId(), postDto.projectId());
        Post post = postRepository.save(postMapper.toEntity(postDto));
        return post.getId();
    }

    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        checkPostIsNotPublishedAndNotDeleted(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        post.setContent(postDto.content());
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public Long deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        checkPostWasNotDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
        return post.getId();
    }

    public PostDto getPost(Long postId) {
        return postRepository.findById(postId)
                .map(postMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    public List<PostDto> getDraftPostsForUser(Long idUser) {
        userContext.setUserId(idUser);
        checkUserExistById(idUser);
        return postRepository.findByAuthorId(idUser)
                .stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getDraftPostsForProject(Long idProject) {
        checkProjectExistById(idProject);
        return postRepository.findByProjectId(idProject)
                .stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getPublishedPostsForUser(Long idUser) {
        userContext.setUserId(idUser);
        checkUserExistById(idUser);
        return postRepository.findByAuthorId(idUser)
                .stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getPublishedPostForProject(Long idProject) {
        checkProjectExistById(idProject);
        return postRepository.findByProjectId(idProject)
                .stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    public void checkGrammarPostContentAndChangeIfNeed() {
        List<Post> posts = getAllUnpublishedPostsOrThrow();
        posts.forEach(post -> {
            String content = post.getContent();
            String result;
            try {
                HttpResponse<String> response = getResponsesWithCorrectText(content);
                result = extractTextFromRequest(response);
                if (extractBooleanSafely(response)) {
                    post.setContent(result);
                    postRepository.save(post);
                    log.info("Post with id: {} was checked for grammar. New content: {}", post.getId(), post.getContent());
                } else {
                    log.error("Response status was false for post id: {}", post.getId());
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Post> getAllUnpublishedPostsOrThrow() {

        List<Post> unpublishedPosts = StreamSupport
                .stream(postRepository.findAll().spliterator(), false)
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .collect(Collectors.toList());
        System.out.println(unpublishedPosts.size());
        if (unpublishedPosts.isEmpty()) {
            log.error("The list of unpublished posts is null.");
            throw new IllegalStateException("The list of unpublished posts is null.");
        }

        return unpublishedPosts;
    }

    private void checkIdUserAndIdProjectNotEquals(Long idUser, Long idProject) {
        if (Objects.equals(idUser, idProject)) {
            log.error(String.format("idProject %s and idUser %s equals", idProject, idUser));
            throw new IllegalArgumentException(String.format("idProject %s and idUser %s equals", idProject, idUser));
        }
    }

    private void checkPostIsNotPublishedAndNotDeleted(Post post) {
        if (post.isPublished()) {
            log.error("Post already published, id:" + post.getId());
            throw new IllegalArgumentException("Post already published, id: " + post.getId());
        }
        if (post.isDeleted()) {
            log.error("Post was deleted, id:" + post.getId());
            throw new IllegalArgumentException("Post was deleted, id: " + post.getId());
        }
    }

    private void checkAuthorIdExist(Long idUser, Long idProject) {
        checkIdUserAndIdProjectNotEquals(idUser, idProject);
        String temp = "User id: ";
        try {
            if (idUser != null) {
                userContext.setUserId(idUser);
                userServiceClient.getUser(idUser);
            } else if (idProject != null) {
                temp = "Project id: ";
                projectServiceClient.getProject(idProject);
            }
        } catch (FeignException e) {
            switch (e.status()) {
                case 404:
                    log.error(temp + idUser + " not found " + HttpStatus.NOT_FOUND);
                    throw new IllegalArgumentException(temp + idUser + " not found" + HttpStatus.NOT_FOUND);
                case 500:
                    log.error(temp + idUser + " Internal Server Error " + HttpStatus.INTERNAL_SERVER_ERROR);
                    throw new IllegalArgumentException(temp + idUser + " Internal Server Error" + HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    log.error(temp + idUser + " Error " + e.getMessage());
                    throw new IllegalArgumentException(temp + idUser + " Error " + e.getMessage());
            }
        }
    }

    private void checkUserExistById(Long idUser) {
        try {
            userServiceClient.getUser(idUser);
        } catch (FeignException e) {
            log.error("User id:" + idUser + "Error" + e.getMessage());
            throw new IllegalArgumentException("User id:" + idUser + "Error" + e.getMessage());
        }
    }

    private void checkProjectExistById(Long idProject) {
        try {
            projectServiceClient.getProject(idProject);
        } catch (FeignException e) {
            log.error("Project id:" + idProject + "Error" + e.getMessage());
            throw new IllegalArgumentException("Project id:" + idProject + "Error" + e.getMessage());
        }
    }

    private void checkPostWasNotDeleted(Post post) {
        if (post.isDeleted()) {
            log.error("Post with id: " + post.getId() + " was deleted");
            throw new IllegalArgumentException("Post with id: " + post.getId() + " was deleted");
        }
    }

    @Retryable(value = {InterruptedException.class, IOException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    private HttpResponse<String> getResponsesWithCorrectText(String text) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://textgears-textgears-v1.p.rapidapi.com/correct"))
                .header("x-rapidapi-key", "52b8fb4f25msh122fa7902a41e9bp1105cejsn54289f3bb474")
                .header("x-rapidapi-host", "textgears-textgears-v1.p.rapidapi.com")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("text=" + text))
                .build();
        int attempts = 0;
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Request failed. Attempt: " + attempts);
            throw new IOException("Request failed. Attempt: " + attempts);
        } catch (InterruptedException e) {
            log.error("Request failed. Attempt: " + attempts);
            throw new InterruptedException("Request failed. Attempt: " + attempts);
        }
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String extractTextFromRequest(HttpResponse<String> response) throws IOException, InterruptedException {
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonResponse.getAsJsonObject("response").get("corrected").getAsString();
    }

    private boolean extractBooleanSafely(HttpResponse<String> response) {
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        try {
            if (jsonResponse.has("status")) {
                return jsonResponse.get("status").getAsBoolean();
            }
        } catch (Exception e) {
            System.err.println("Error extracting boolean: " + e.getMessage());
        }
        return false;
    }

}