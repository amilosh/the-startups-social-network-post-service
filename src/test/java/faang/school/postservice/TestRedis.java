package faang.school.postservice;


import faang.school.postservice.repository.NewsFeedRepository;
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
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NewsFeedRepository newsFeedRepository;

    private static final Long KEY = 1234L;

    private long counter = 0;

    @Test
    public void test() throws InterruptedException {
        List<Long> values = newsFeedRepository.getPostBatch(KEY, 3L);
        values.forEach(val -> System.out.print(val + " "));
        System.out.println();

        Thread one = new Thread(() -> newsFeedRepository.addPost(100L, KEY, counter++));
        Thread two = new Thread(() -> newsFeedRepository.addPost(200L, KEY, counter++));

        one.start();
        two.start();

        one.join();
        two.join();

        List<Long> newValues = newsFeedRepository.getPostBatch(KEY);
        newValues.forEach(val -> System.out.print(val + " "));
        System.out.println();
    }
}
