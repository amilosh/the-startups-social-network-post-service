package faang.school.postservice.scheduled;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "0/4 * * * * *")
    public void checkSpelling() {
        postService.checkSpelling();
    }

}
