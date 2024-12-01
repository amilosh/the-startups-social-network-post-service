package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrector {

    private final PostService postService;

    @Async("my-executor")
    @Scheduled(cron = "${spring.crontab.checkPostsGrammar}")
    public void checkGrammarPosts() {
        postService.checkGrammarPostContentAndChangeIfNeed();
    }

}
