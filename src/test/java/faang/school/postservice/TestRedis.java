package faang.school.postservice;


import faang.school.postservice.repository.NewsFeedRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Slf4j
public class TestRedis {

    @Autowired
    private NewsFeedRedisRepository newsFeedRedisRepository;

    private static final Long KEY = 1234L;

    private int counter = 0;

    @Test
    public void test() throws InterruptedException {


        Thread one = new Thread(() -> newsFeedRedisRepository.addPostId(100L, KEY, counter++));
        Thread two = new Thread(() -> newsFeedRedisRepository.addPostId(200L, KEY, counter++));
        Thread three = new Thread(() -> newsFeedRedisRepository.addPostId(300L, KEY, counter++));
        Thread four = new Thread(() -> newsFeedRedisRepository.addPostId(400L, KEY, counter++));

        one.start();
        two.start();
        three.start();
        four.start();

        one.join();
        two.join();
        three.join();
        four.join();

        List<Long> postIds = newsFeedRedisRepository.getPostIdsFirstBatch(KEY);
        System.out.println(postIds);
        List<Long> newPostIds = newsFeedRedisRepository.getPostIdsBatch(KEY, 200L);
        System.out.println(newPostIds);
    }
}
