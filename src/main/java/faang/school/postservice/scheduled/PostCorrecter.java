package faang.school.postservice.scheduled;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/")
public class PostCorrecter {

    private final PostService postService;
    private final PostRepository postRepository;

//    @Scheduled(cron = "0/4 * * * * *")

    @GetMapping("check")
    public void checkSpelling() {
//        List<Post> posts = postRepository.findByPublishedFalse();
//        posts.forEach(postService::checkSpelling);
        Post post = Post.builder().content("This is errror. From exeption").build();
        Post post2 = Post.builder().content("This is error").build();
        List<Post> posts = List.of(post, post2);
        posts = postService.checkSpelling(posts);
        postRepository.saveAll(posts);
    }

}
