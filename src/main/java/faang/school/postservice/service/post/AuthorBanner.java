package faang.school.postservice.service.post;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorBanner {

    private PostService postService;

    @Scheduled(cron = "${cron.expression}")
    public void checkUnverifiedPosts() {
        postService.getPostsWhereVerifiedFalse();
    }

}
