package faang.school.postservice.scheduled;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;
    private final PostRepository postRepository;

    @Scheduled(cron = "#{schedulingProperties.cron}")
    @Retryable(retryFor = ResourceAccessException.class, maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void checkSpelling() {
        List<Post> posts = postRepository.findByPublishedFalse();
        posts = postService.checkSpelling(posts);
        postRepository.saveAll(posts);
    }

}
