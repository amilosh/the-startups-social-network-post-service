package faang.school.postservice.service.post;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final String API_KEY = "c2b61bcbe1msh7baed25073c44a3p111bcfjsn7841c9808baa";
    private final String API_ENDPOINT = "https://jspell-checker.p.rapidapi.com/check";

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public PostResponseDto createPost(PostRequestDto postDto) {
        isPostAuthorExist(postDto);

        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post.setDeleted(false);

        postRepository.save(post);
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

    @Retryable(retryFor = ResourceAccessException.class, maxAttempts = 4,
            backoff = @Backoff(delay = 1000,multiplier = 2))
    @Async("taskExecutor")
    public void checkSpelling(Post post) {
        String json = "{"
                + " \"language\": \"enUS\","
                + " \"fieldvalues\": \"" + post.getContent().replace("\"", "\\\"") + "\","
                + " \"config\": {"
                + "     \"forceUpperCase\": false,"
                + "     \"ignoreIrregularCaps\": false,"
                + "     \"ignoreFirstCaps\": true,"
                + "     \"ignoreNumbers\": true,"
                + "     \"ignoreUpper\": false,"
                + "     \"ignoreDouble\": false,"
                + "     \"ignoreWordsWithNumbers\": true"
                + " }"
                + "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json");
        headers.set("x-rapidapi-host", "jspell-checker.p.rapidapi.com");
        headers.set("x-rapidapi-key", API_KEY);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(API_ENDPOINT, requestEntity, String.class);
        JSONObject jsonObject = new JSONObject(response);
        int errorCount = jsonObject.getInt("spellingErrorCount");
        if (errorCount == 0) {
            return;
        }
        setCorrectContent(jsonObject, post);
        log.info("Added corrected content {} to the post {}", post.getContent(), post.getId());
        postRepository.save(post);
    }

    private Post setCorrectContent(JSONObject jsonObject, Post post) {
        JSONArray elementsArray = jsonObject.getJSONArray("elements");
        JSONObject firstElement = elementsArray.getJSONObject(0);
        JSONArray errorsArray = firstElement.getJSONArray("errors");
        JSONObject firstError = errorsArray.getJSONObject(0);
        JSONArray suggestionsArray = firstError.getJSONArray("suggestions");
        String firstSuggestion = suggestionsArray.getString(0);
        post.setContent(firstSuggestion);
        return post;
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
