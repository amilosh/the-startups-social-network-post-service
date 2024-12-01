package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.config.api.SpellingConfig;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.filter.PostFilters;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    ExecutorService execute = Executors.newFixedThreadPool(10);

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final SpellingConfig api;
    private final RestTemplate restTemplate;
    private final PostValidator postValidator;
    private final List<PostFilters> postFilters;

    public PostResponseDto create(PostRequestDto postRequestDto) {
        postValidator.validateCreate(postRequestDto);

        Post post = postMapper.toEntity(postRequestDto);

        post.setPublished(false);
        post.setDeleted(false);
        Post savePost = postRepository.save(post);

        return  postMapper.toDto(savePost);
    }

    public PostResponseDto publishPost(Long id) {
        Post post = postValidator.validateAndGetPostById(id);
        postValidator.validatePublish(post);
        post.setPublished(true);
        post.setDeleted(false);

        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto updatePost(PostUpdateDto postDto) {
        Objects.requireNonNull(postDto, "PostUpdateDto cannot be null");

        Post post = postValidator.validateAndGetPostById(postDto.getId());
        post.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(post));
    }

    public void deletePost(Long id) {
        Post post = postRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postValidator.validateDelete(post);

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostResponseDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void checkSpelling() {
        List<Post> posts = postRepository.findByPublishedFalse();
        int sizeOfRequests = getSizeOfRequest(posts.size());
        for (int i = 0; i < posts.size(); i += sizeOfRequests) {
            List<Post> sublist = posts.subList(i, Math.min(i + sizeOfRequests, posts.size()));
            checkingPostsForSpelling(sublist);
        }
    }

    private void checkingPostsForSpelling(List<Post> posts) {
        String jsonPayload = getJsonFromPosts(posts);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", api.getContent());
        headers.set("x-rapidapi-host", api.getHost());
        headers.set("x-rapidapi-key", api.getKey());
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
        try {
            String response = restTemplate.postForObject(api.getEndpoint(), requestEntity, String.class);
            JSONObject jsonObject = new JSONObject(response);
            int errorCount = jsonObject.getInt("spellingErrorCount");
            if (errorCount == 0) {
                log.info("No errors found in post content");
                return;
            }
            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                int finalI = i;
                execute.execute(() -> setCorrectContent(jsonObject, post, finalI));
            }
            execute.shutdown();
            if (!execute.awaitTermination(2, TimeUnit.MINUTES)) {
                execute.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("An interrupt error occurred in the method of checking the spelling ", e);
            execute.shutdownNow();
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            log.error("An error occurred while executing a request to an external server ", e);
            throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
        } catch (JSONException e) {
            log.error("An error occurred while processing JSON content. ", e);
            throw new JSONException(e);
        }
    }

    private String getJsonFromPosts(List<Post> posts) {
        List<String> contentFromPosts = new ArrayList<>();
        posts.forEach(post -> contentFromPosts.add(post.getContent()));
        JSONObject json = new JSONObject();
        json.put("language", "enUS");
        JSONArray fieldvalues = new JSONArray();
        contentFromPosts.forEach(content -> fieldvalues.put(escapeJson(content)));
        json.put("fieldvalues", fieldvalues);
        JSONObject config = new JSONObject();
        config.put("forceUpperCase", false)
                .put("ignoreIrregularCaps", false)
                .put("ignoreFirstCaps", true)
                .put("ignoreNumbers", true)
                .put("ignoreUpper", false)
                .put("ignoreDouble", false)
                .put("ignoreWordsWithNumbers", true);
        json.put("config", config);
        return json.toString();
    }

    private void setCorrectContent(JSONObject jsonObject, Post post, int id) {
        try {
            String content = post.getContent();
            JSONArray elementsArray = jsonObject.getJSONArray("elements");
            JSONObject firstElement = elementsArray.getJSONObject(id);
            JSONArray errorsArray = firstElement.getJSONArray("errors");
            int size = errorsArray.length();
            if (size == 0) {
                return;
            }
            for (int i = 0; i < size; i++) {
                JSONObject error = errorsArray.getJSONObject(i);
                String word = error.getString("word");
                JSONArray suggestionsArray = error.getJSONArray("suggestions");
                String correctWord = suggestionsArray.getString(0);
                content = content.replace(word, correctWord);
            }
            post.setContent(content);
            postRepository.save(post);
            log.info("Added corrected content {} to the post {}", post.getContent(), post.getId());
        } catch (Exception e) {
            log.error("An error occurred while processing the post {}", post.getId(), e);
        }
    }

    private int getSizeOfRequest(int sizeOfPosts) {
        if (sizeOfPosts <= 100) {
            return 10;
        } else if (sizeOfPosts <= 500) {
            return 50;
        } else return 100;
    }

    private static String escapeJson(String data) {
        return data.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    public List<PostResponseDto> getPosts(PostFilterDto filterDto) {
        Stream<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false);

        postFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(posts, filterDto));

        return postMapper.toDtoList(posts.toList());
    }
}
