package faang.school.postservice;


import faang.school.postservice.dto.post.FeedPost;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.NewsFeedService;
import faang.school.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
@Slf4j
public class TestRedis {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostRedisRepository postRedisRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private NewsFeedService newsFeedService;

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            Post post = Post.builder()
                    .content("aaa")
                    .authorId(1L)
                    .published(true)
                    .publishedAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        for (int i = 0; i < 4; i++) {
            Post post = Post.builder()
                    .content("aaa")
                    .authorId(2L)
                    .published(true)
                    .publishedAt(LocalDateTime.now())
                    .build();
            postRepository.save(post);
        }

        for (int i = 0; i < 4; i++) {
            Post post = Post.builder()
                    .content("aaa")
                    .authorId(1L)
                    .build();
            Post saved = postService.createDraftPost(post);
            postService.publishPost(saved.getId());
        }

        for (int i = 0; i < 4; i++) {
            Post post = Post.builder()
                    .content("aaa")
                    .authorId(2L)
                    .build();
            Post saved = postService.createDraftPost(post);
            postService.publishPost(saved.getId());
        }

        Thread.sleep(5000);


        List<FeedPost> feedPosts = newsFeedService.getFeedBatch(3L, null);
        feedPosts.forEach(System.out::println);
    }



}
