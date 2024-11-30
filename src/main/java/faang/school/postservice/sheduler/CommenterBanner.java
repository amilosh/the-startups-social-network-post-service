package faang.school.postservice.sheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommenterBanner {
    private final PostService postService;

    @Scheduled(fixedDelay = 5000)
    public void banForComments() {
        log.info("Starting CommenterBanner");
        postService.allAuthorIdWithNotVerifyComments();
    }
}
