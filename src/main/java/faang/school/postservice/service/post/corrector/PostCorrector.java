package faang.school.postservice.service.post.corrector;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@RequiredArgsConstructor
public class PostCorrector {
    private final PostService postService;

    @Scheduled(cron = "${auto-start-syntax.cron}")
    @Retryable(retryFor = {IOException.class, InterruptedException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void startCheckingPosts() throws IOException, InterruptedException {
        postService.checkingPostForErrors();
    }
}
